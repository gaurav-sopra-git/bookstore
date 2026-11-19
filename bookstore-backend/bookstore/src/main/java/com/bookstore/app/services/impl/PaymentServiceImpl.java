package com.bookstore.app.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.app.dtos.PaymentDtos.CheckoutSessionResponse;
import com.bookstore.app.entities.CustomerOrder;
import com.bookstore.app.entities.Payment;
import com.bookstore.app.enums.PaymentProvider;
import com.bookstore.app.enums.PaymentStatus;
import com.bookstore.app.events.OrderPlacedEvent;
import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.repositories.CustomerOrderRepository;
import com.bookstore.app.repositories.PaymentRepository;
import com.bookstore.app.services.PaymentGateway;
import com.bookstore.app.services.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final CustomerOrderRepository orderRepo;
	private final PaymentRepository paymentRepo;
	private final PaymentGateway paymentGateway;
	private final ApplicationEventPublisher eventPublisher;

	public PaymentServiceImpl(CustomerOrderRepository orders, PaymentRepository payments,
			PaymentGateway paymentGateway, ApplicationEventPublisher eventPublisher) {
		this.orderRepo = orders;
		this.paymentRepo = payments;
		this.paymentGateway = paymentGateway;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public CheckoutSessionResponse initiateCheckout(Long userId, String orderNumber) {

		CustomerOrder order = orderRepo.findByOrderNumberAndUser_Id(orderNumber, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		if (order.getStatus() != com.bookstore.app.enums.OrderStatus.PENDING_PAYMENT) {
			throw new IllegalStateException("Only pending-payment orders can be paid");
		}

		List<PaymentStatus> activeStatuses = List.of(PaymentStatus.CREATED, PaymentStatus.REQUIRES_ACTION,
				PaymentStatus.PROCESSING);

		Payment payment = paymentRepo
				.findFirstByCustomerOrder_IdAndStatusInOrderByCreatedAtDesc(order.getId(), activeStatuses)
				.orElse(null);

		// Reuse an existing active Checkout Session.
		if (payment != null && payment.getCheckoutSessionId() != null && payment.getCheckoutUrl() != null) {

			return new CheckoutSessionResponse(orderNumber, payment.getCheckoutUrl(), payment.getStatus().name());
		}

		// Create a new payment attempt if none exists.
		if (payment == null) {
			long amountMinor = toMinorUnits(order.getTotalAmount());

			payment = Payment.create(order, PaymentProvider.STRIPE, amountMinor, order.getCurrency(),
					UUID.randomUUID().toString());

			payment = paymentRepo.save(payment);
		}

		var request = new PaymentGateway.CheckoutPaymentRequest(order.getOrderNumber(), payment.getAmountMinor(),
				payment.getCurrency(), "Papertrail order " + order.getOrderNumber(), payment.getIdempotencyKey());

		var session = paymentGateway.createCheckoutSession(request);

		payment.attachCheckoutSession(session.sessionId(), session.checkoutUrl());

		return new CheckoutSessionResponse(orderNumber, session.checkoutUrl(), payment.getStatus().name());
	}
	
	
	@Override
	@Transactional
	public void handleCheckoutCompleted(
	        String sessionId,
	        String paymentIntentId,
	        Long amountTotal,
	        String currency,
	        String paymentStatus) {

	    Payment payment = paymentRepo.findByCheckoutSessionId(sessionId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Payment not found for Stripe session"));

	    // Only fulfil an actually paid Checkout Session.
	    if (!"paid".equalsIgnoreCase(paymentStatus)) {
	        return;
	    }

	    // Verify the amount and currency against our own persisted payment.
	    if (amountTotal == null
	            || !amountTotal.equals(payment.getAmountMinor())
	            || currency == null
	            || !currency.equalsIgnoreCase(payment.getCurrency())) {
	        throw new IllegalStateException(
	                "Stripe payment amount or currency does not match the order");
	    }

	    CustomerOrder order = payment.getCustomerOrder();

	    // Duplicate webhook delivery must not confirm or publish twice.
	    if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
	        return;
	    }

	    payment.markSucceeded(paymentIntentId);
	    order.confirm();

	    eventPublisher.publishEvent(
	            OrderPlacedEvent.from(
	                    order.getOrderNumber(),
	                    order.getUser().getId(),
	                    order.getTotalAmount()
	            )
	    );
	}

	@Override
	@Transactional
	public void handleAsyncPaymentFailed(String sessionId) {

	    Payment payment = paymentRepo.findByCheckoutSessionId(sessionId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Payment not found for Stripe session"));

	    // A late failure notification must never downgrade a successful payment.
	    if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
	        return;
	    }

	    payment.markFailed();

	    // Keep the order pending so the customer can retry payment.
	    // Inventory is NOT restored here.
	}

	private long toMinorUnits(BigDecimal amount) {
		return amount.movePointRight(2).longValueExact();
	}
}