/*
 * package com.bookstore.app.services;
 * 
 * import java.math.BigDecimal;
 * 
 * public interface PaymentGateway {
 * 
 * CheckoutSession createCheckoutSession( CheckoutPaymentRequest request );
 * 
 * record CheckoutPaymentRequest( String orderNumber, Long amountMinor, String
 * currency, String productName, String idempotencyKey ) {}
 * 
 * record CheckoutSession( String sessionId, String checkoutUrl ) {}
 * 
 * 
 * public record PaymentRequest(String orderNumber, BigDecimal amount, Long
 * amountMinor, String currency, String productName, String idempotencyKey) { }
 * }
 */