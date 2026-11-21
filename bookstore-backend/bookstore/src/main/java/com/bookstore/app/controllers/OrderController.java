package com.bookstore.app.controllers;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dtos.OrderDtos.OrderResponse;
import com.bookstore.app.security.AuthenticatedUser;
import com.bookstore.app.services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	
	private final OrderService service;

	public OrderController(OrderService service) {
		this.service = service;
	}

	@GetMapping
	public List<OrderResponse> getOrders(Authentication auth) {
		return service.getOrders(((AuthenticatedUser) auth.getPrincipal()).id());
	}

	@GetMapping("/{orderNumber}")
	public OrderResponse getOrder(@PathVariable String orderNumber, Authentication auth) {
		return service.getOrder(((AuthenticatedUser) auth.getPrincipal()).id(), orderNumber);
	}
}
