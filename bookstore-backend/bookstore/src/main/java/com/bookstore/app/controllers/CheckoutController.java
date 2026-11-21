package com.bookstore.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dtos.OrderDtos.CheckoutRequest;
import com.bookstore.app.dtos.OrderDtos.OrderResponse;
import com.bookstore.app.security.AuthenticatedUser;
import com.bookstore.app.services.OrderService;

import jakarta.validation.Valid;

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

