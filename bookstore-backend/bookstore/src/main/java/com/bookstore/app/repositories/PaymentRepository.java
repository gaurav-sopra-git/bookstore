package com.bookstore.app.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.app.entities.Payment;
import com.bookstore.app.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByIdempotencyKey(String idempotencyKey);

	Optional<Payment> findByCheckoutSessionId(String checkoutSessionId);

	Optional<Payment> findByProviderPaymentId(String providerPaymentId);
	
	Optional<Payment> findFirstByCustomerOrder_OrderNumberAndStatusOrderByCreatedAtDesc(String orderNumber,
			PaymentStatus status);
	
	Optional<Payment> findFirstByCustomerOrder_IdAndStatusInOrderByCreatedAtDesc(Long orderId,
			Collection<PaymentStatus> statuses);
}