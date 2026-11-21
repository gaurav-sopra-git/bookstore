package com.bookstore.app.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dtos.CartDtos.AddItemRequest;
import com.bookstore.app.dtos.CartDtos.CartResponse;
import com.bookstore.app.dtos.CartDtos.UpdateQuantityRequest;
import com.bookstore.app.security.AuthenticatedUser;
import com.bookstore.app.services.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	private final CartService service;

	public CartController(CartService service) {
		this.service = service;
	}

	@PostMapping("/guest")
	public ResponseEntity<CartResponse> createGuestCart() {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createGuestCart());
	}

	@GetMapping
	public CartResponse getCart(Authentication auth,
			@RequestHeader(value = "X-Cart-Token", required = false) String token) {
		return service.getCart(userId(auth), token);
	}

	@PostMapping("/items")
	public ResponseEntity<CartResponse> add(@Valid @RequestBody AddItemRequest request, Authentication auth,
			@RequestHeader(value = "X-Cart-Token", required = false) String token) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(service.addItem(userId(auth), token, request.bookId(), request.quantity()));
	}

	@PutMapping("/items/{bookId}")
	public CartResponse update(@PathVariable Long bookId, @Valid @RequestBody UpdateQuantityRequest request,
			Authentication auth, @RequestHeader(value = "X-Cart-Token", required = false) String token) {
		return service.updateQuantity(userId(auth), token, bookId, request.quantity());
	}

	@DeleteMapping("/items/{bookId}")
	public CartResponse remove(@PathVariable Long bookId, Authentication auth,
			@RequestHeader(value = "X-Cart-Token", required = false) String token) {
		return service.removeItem(userId(auth), token, bookId);
	}

	private Long userId(Authentication auth) {
		return auth != null && auth.getPrincipal() instanceof AuthenticatedUser u ? u.id() : null;
	}
}
