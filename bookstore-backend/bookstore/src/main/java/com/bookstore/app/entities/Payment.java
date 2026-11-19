package com.bookstore.app.entities;

import java.time.Instant;
import java.util.Currency;

import com.bookstore.app.enums.PaymentProvider;
import com.bookstore.app.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "payments", uniqueConstraints = {
		@UniqueConstraint(name = "uk_payment_idempotency_key", columnNames = "idempotency_key"),
		@UniqueConstraint(name = "uk_provider_payment", columnNames = { "provider", "provider_payment_id" }),
		@UniqueConstraint(name = "uk_checkout_session", columnNames = "checkout_session_id")})

public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id", nullable = false)
	private CustomerOrder customerOrder;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private PaymentProvider provider;

	@Column(name = "provider_payment_id", length = 150)
	private String providerPaymentId;

	@Column(name = "provider_order_id", length = 150)
	private String providerOrderId;

	@Column(name = "amount_minor", nullable = false)
	private Long amountMinor;

	@Column(nullable = false, length = 3)
	private String currency;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private PaymentStatus status;

	@Column(name = "idempotency_key", nullable = false, length = 100)
	private String idempotencyKey;

	@Column(name = "checkout_session_id", length = 150)
	private String checkoutSessionId;
	
	@Column(name = "checkout_url", length = 1000)
	private String checkoutUrl;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	public static Payment create(CustomerOrder order, PaymentProvider provider, Long amountMinor, String currency,
			String idempotencyKey) {

		if (order == null || provider == null) {
			throw new IllegalArgumentException("Order and provider are required");
		}

		if (amountMinor == null || amountMinor <= 0) {
			throw new IllegalArgumentException("Payment amount must be positive");
		}

		Currency.getInstance(currency);

		if (idempotencyKey == null || idempotencyKey.isBlank()) {
			throw new IllegalArgumentException("Idempotency key is required");
		}

		var payment = new Payment();
		payment.customerOrder = order;
		payment.provider = provider;
		payment.amountMinor = amountMinor;
		payment.currency = currency.toUpperCase();
		payment.idempotencyKey = idempotencyKey;
		payment.status = PaymentStatus.CREATED;

		return payment;
	}

	public void attachCheckoutSession(String sessionId, String checkoutUrl) {
		if (sessionId == null || sessionId.isBlank()
				|| checkoutUrl == null || checkoutUrl.isBlank()) {
			throw new IllegalArgumentException("Checkout session ID and URL is required");
		}

		if (this.checkoutSessionId != null) {
			if (this.checkoutSessionId.equals(sessionId)) {
				return;
			}
			throw new IllegalStateException("Checkout session already attached");
		}

		this.checkoutSessionId = sessionId;
		this.checkoutUrl = checkoutUrl;
	}

	public void markSucceeded(String paymentIntentId) {
		if (status == PaymentStatus.SUCCEEDED) {
			return;
		}

		if (status == PaymentStatus.CANCELLED || status == PaymentStatus.REFUNDED) {
			throw new IllegalStateException("Cannot succeed a cancelled or refunded payment");
		}

		this.providerPaymentId = paymentIntentId;
		this.status = PaymentStatus.SUCCEEDED;
	}

	public void markFailed() {
		if (status == PaymentStatus.SUCCEEDED) {
			throw new IllegalStateException("A successful payment cannot be marked failed");
		}

		if (status != PaymentStatus.CANCELLED) {
			this.status = PaymentStatus.FAILED;
		}
	}

	@PrePersist
	protected void onCreate() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = Instant.now();
	}
}