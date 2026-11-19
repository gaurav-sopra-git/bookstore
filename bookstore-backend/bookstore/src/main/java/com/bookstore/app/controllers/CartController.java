package com.bookstore.app.controllers;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.bookstore.app.dtos.CartDtos.*;
import com.bookstore.app.security.AuthenticatedUser;
import com.bookstore.app.services.CartService;

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
