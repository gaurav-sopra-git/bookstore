package com.bookstore.app.strategies;

import com.bookstore.app.dtos.PaymentDtos.PaymentRequest;
import com.bookstore.app.dtos.PaymentDtos.PaymentResponse;
import com.bookstore.app.enums.PaymentProvider;

public interface PaymentStrategy {
	
	
	PaymentProvider getPaymentProvider();
	
	PaymentResponse  createCheckoutSession(PaymentRequest request);
}
