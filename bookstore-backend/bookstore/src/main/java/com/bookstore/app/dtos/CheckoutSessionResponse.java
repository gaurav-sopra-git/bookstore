package com.bookstore.app.dtos;

public record CheckoutSessionResponse(String orderNumber, String checkoutUrl, String paymentStatus) {
}
