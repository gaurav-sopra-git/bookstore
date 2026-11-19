package com.bookstore.app.services;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(
            CheckoutPaymentRequest request
    );

    record CheckoutPaymentRequest(
            String orderNumber,
            Long amountMinor,
            String currency,
            String productName,
            String idempotencyKey
    ) {}

    record CheckoutSession(
            String sessionId,
            String checkoutUrl
    ) {}
}
