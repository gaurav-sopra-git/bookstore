package com.bookstore.app.dtos;

import java.math.BigDecimal;

public final class PaymentDtos {

	private PaymentDtos() {
	}

	public record PaymentRequest(String orderNumber, BigDecimal amount, Long amountMinor, String currency,
			String productName, String idempotencyKey) {
	}

	public record PaymentResponse(String sessionId, String checkoutUrl) {

	}
}
