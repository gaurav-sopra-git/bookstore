package com.bookstore.app.services;

import com.bookstore.app.dtos.PaymentDtos.CheckoutSessionResponse;

public interface PaymentService {

	CheckoutSessionResponse initiateCheckout(Long userId, String orderNumber);
	
	void handleCheckoutCompleted(String sessionId, String paymentIntentId, Long amountTotal, String currency,
			String paymentStatus);

	void handleAsyncPaymentFailed(String sessionId);
}