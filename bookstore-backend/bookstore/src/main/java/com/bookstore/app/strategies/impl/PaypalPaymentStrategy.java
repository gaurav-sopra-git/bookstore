package com.bookstore.app.strategies.impl;

import com.bookstore.app.dtos.PaymentDtos.PaymentRequest;
import com.bookstore.app.dtos.PaymentDtos.PaymentResponse;
import com.bookstore.app.enums.PaymentProvider;
import com.bookstore.app.strategies.PaymentStrategy;

public class PaypalPaymentStrategy implements PaymentStrategy {


	@Override
	public PaymentProvider getPaymentProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaymentResponse createCheckoutSession(PaymentRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
