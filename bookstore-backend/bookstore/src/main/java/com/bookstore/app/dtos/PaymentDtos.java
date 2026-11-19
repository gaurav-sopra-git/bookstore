package com.bookstore.app.dtos;

public final class PaymentDtos {

	private PaymentDtos() {
	}

	public record CheckoutSessionResponse(String orderNumber, String checkoutUrl, String paymentStatus) {
	}
}