package com.bookstore.app.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.bookstore.app.dtos.PaymentDtos.CheckoutSessionResponse;
import com.bookstore.app.security.AuthenticatedUser;
import com.bookstore.app.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping("/{orderNumber}/checkout-session")
	public ResponseEntity<CheckoutSessionResponse> initiateCheckout(@PathVariable String orderNumber,
			Authentication authentication) {

		AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

		return ResponseEntity.ok(paymentService.initiateCheckout(user.id(), orderNumber));
	}
}