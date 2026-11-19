package com.bookstore.app.controllers;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.bookstore.app.dtos.OrderDtos.*;
import com.bookstore.app.security.AuthenticatedUser;
import com.bookstore.app.services.OrderService;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
	
	private final OrderService service;

	public CheckoutController(OrderService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request, Authentication auth) {
		AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(service.checkout(user.id(), request.shippingAddress()));
	}
}

