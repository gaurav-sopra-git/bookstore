package com.bookstore.app.services.impl;

import static com.bookstore.app.test.helpers.TestData.book;
import static com.bookstore.app.test.helpers.TestData.user;
import static com.bookstore.app.test.helpers.TestData.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.bookstore.app.dtos.CheckoutSessionResponse;
import com.bookstore.app.dtos.PaymentDtos.PaymentRequest;
import com.bookstore.app.dtos.PaymentDtos.PaymentResponse;
import com.bookstore.app.entities.CustomerOrder;
import com.bookstore.app.entities.OrderItem;
import com.bookstore.app.entities.Payment;
import com.bookstore.app.enums.PaymentProvider;
import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.repositories.CustomerOrderRepository;
import com.bookstore.app.repositories.PaymentRepository;
import com.bookstore.app.strategies.PaymentStrategy;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

	private static final String SESSION_URL = "https://checkout.stripe.com/c/pay/cs_test_1";

	@Mock
	private CustomerOrderRepository orders;
	@Mock
	private PaymentRepository payments;
	@Mock
	private ApplicationEventPublisher events;
	@Mock
	private PaymentStrategy paymentStrategy;
	
	private List<PaymentStrategy> paymentStrategies;

	private PaymentServiceImpl service;
	
	private CustomerOrder order;
	private PaymentStrategy strategyUsed ;

	@BeforeEach
	void setUp() {
		paymentStrategies = List.of(paymentStrategy);
		service = new PaymentServiceImpl(orders, payments,  events, paymentStrategies);
		order = CustomerOrder.place(withId(user("asha@example.com"), 1L),  "ORD-1", "12 Park Street", "USD");
		order.addItem(OrderItem.snapshotOf(book("Title", "name surname", "499.00", 5), new BigDecimal("499.00"), 2));
		withId(order, 5L);
		strategyUsed = strategyUsed();
	}


	@Test
	void initiateCheckout_createsAPaymentAndAStripeSession() {
		when(orders.findByOrderNumberAndUser_Id("ORD-1", 1L)).thenReturn(Optional.of(order));
		when(payments.findFirstByCustomerOrder_IdAndStatusInOrderByCreatedAtDesc(eq(5L), anyCollection()))
				.thenReturn(Optional.empty());
		when(payments.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
		
		
		when(strategyUsed.createCheckoutSession(any()))
				.thenReturn(new PaymentResponse("cs_test_1", SESSION_URL));

		when(strategyUsed.getPaymentProvider()).thenReturn(PaymentProvider.STRIPE);
		CheckoutSessionResponse response = service.initiateCheckout(1L, "ORD-1", "STRIPE");

		assertThat(response.checkoutUrl()).isEqualTo(SESSION_URL);
		assertThat(response.paymentStatus()).isEqualTo("CREATED");

		ArgumentCaptor<PaymentRequest> request = ArgumentCaptor
				.forClass(PaymentRequest.class);
		verify(strategyUsed).createCheckoutSession(request.capture());
		assertThat(request.getValue().orderNumber()).isEqualTo("ORD-1");
		assertThat(request.getValue().amountMinor()).isEqualTo(99_800L);
		assertThat(request.getValue().currency()).isEqualTo("USD");
	}


	private PaymentStrategy strategyUsed() {
		PaymentStrategy strategyUsed = paymentStrategies.stream()
		.findFirst()
		.get();
		return strategyUsed;
	}

	@Test
	void initiateCheckout_reusesAnExistingSessionInsteadOfCreatingAnother() {
		Payment existing = paymentWithSession();
		when(orders.findByOrderNumberAndUser_Id("ORD-1", 1L)).thenReturn(Optional.of(order));
		when(payments.findFirstByCustomerOrder_IdAndStatusInOrderByCreatedAtDesc(eq(5L), anyCollection()))
				.thenReturn(Optional.of(existing));

		CheckoutSessionResponse response = service.initiateCheckout(1L, "ORD-1", "STRIPE");

		assertThat(response.checkoutUrl()).isEqualTo(SESSION_URL);
		verifyNoInteractions(strategyUsed);
		verify(payments, never()).save(any());
	}

	@Test
	void initiateCheckout_forAnUnknownOrder_throwsNotFound() {
		when(orders.findByOrderNumberAndUser_Id("ORD-404", 1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.initiateCheckout(1L, "ORD-404", "STRIPE"))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void initiateCheckout_forAnOrderThatIsAlreadyConfirmed_isRejected() {
		order.confirm();
		when(orders.findByOrderNumberAndUser_Id("ORD-1", 1L)).thenReturn(Optional.of(order));

		assertThatThrownBy(() -> service.initiateCheckout(1L, "ORD-1", "STRIPE"))
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("pending-payment");

		verifyNoInteractions(payments, strategyUsed);
	}

	private Payment paymentWithSession() {
		Payment payment = Payment.create(order, PaymentProvider.STRIPE, 99_800L, "USD", "idem-1");
		payment.attachCheckoutSession("cs_test_1", SESSION_URL);
		return payment;
	}
}
