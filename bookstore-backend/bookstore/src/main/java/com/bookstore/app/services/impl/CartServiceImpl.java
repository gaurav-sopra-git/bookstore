package com.bookstore.app.services.impl;

import java.math.BigDecimal;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bookstore.app.dtos.CartDtos.*;
import com.bookstore.app.entities.*;
import com.bookstore.app.mappers.CartMapper;
import com.bookstore.app.exceptions.*;
import com.bookstore.app.repositories.*;
import com.bookstore.app.services.CartService;

@Service
public class CartServiceImpl implements CartService {
	
	private final CartRepository carts;
	private final BookRepository books;
	private final UserRepository users;
	private final CartMapper mapper;

	public CartServiceImpl(CartRepository carts, BookRepository books, UserRepository users, CartMapper mapper) {
		this.carts = carts;
		this.books = books;
		this.users = users;
		this.mapper = mapper;
	}

	@Transactional
	public CartResponse createGuestCart() {
		Cart cart = Cart.guest(UUID.randomUUID().toString());
		return toResponse(carts.save(cart));
	}

	@Transactional(readOnly = true)
	public CartResponse getCart(Long userId, String token) {
		if (userId == null && (token == null || token.isBlank()))
			return empty(token);
		if (userId != null && carts.findByUser_Id(userId).isEmpty())
			return empty(null);
		return toResponse(findCart(userId, token));
	}

	@Transactional
	public CartResponse addItem(Long userId, String token, Long bookId, int quantity) {
		if (quantity < 1)
			throw new BusinessException("Quantity must be at least 1");
		Cart cart = resolveOrCreate(userId, token);
		Book book = books.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
		CartItem existing = cart.getItems().stream().filter(i -> i.getBook().getId().equals(bookId)).findFirst()
				.orElse(null);
		int newQty = quantity + (existing == null ? 0 : existing.getQuantity());
		if (newQty > book.getStock())
			throw new BusinessException("Requested quantity exceeds available stock for: " + book.getTitle());
		if (existing == null) {
			CartItem item = CartItem.forBook(book, quantity);
			cart.addItem(item);
		} else
			existing.changeQuantity(newQty);
		return toResponse(carts.save(cart));
	}

	@Transactional
	public CartResponse updateQuantity(Long userId, String token, Long bookId, int quantity) {
		if (quantity < 1)
			throw new BusinessException("Quantity must be at least 1");
		Cart cart = findCart(userId, token);
		CartItem item = findItem(cart, bookId);
		if (quantity > item.getBook().getStock())
			throw new BusinessException("Requested quantity exceeds available stock");
		item.changeQuantity(quantity);
		return toResponse(cart);
	}

	@Transactional
	public CartResponse removeItem(Long userId, String token, Long bookId) {
		Cart cart = findCart(userId, token);
		cart.getItems().stream().filter(i -> i.getBook().getId().equals(bookId)).findFirst()
				.ifPresent(cart::removeItem);
		return toResponse(cart);
	}

	@Transactional
	public void clear(Long userId) {
		carts.findByUser_Id(userId).ifPresent(c -> {
			c.clearItems();
		});
	}

	private Cart resolveOrCreate(Long userId, String token) {
		if (userId != null) {
			User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
			return carts.findByUserIdWithItems(userId).orElseGet(() -> {
				Cart c = Cart.forUser(user);
				return carts.save(c);
			});
		}
		if (token == null || token.isBlank())
			throw new BusinessException("X-Cart-Token header is required for guest carts");
		return carts.findByTokenWithItems(token).orElseGet(() -> {
			Cart c = Cart.guest(token);
			return carts.save(c);
		});
	}

	private Cart findCart(Long userId, String token) {
		if (userId != null)
			return carts.findByUserIdWithItems(userId)
					.orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
		if (token == null || token.isBlank())
			throw new ResourceNotFoundException("Cart not found");
		return carts.findByTokenWithItems(token).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
	}

	private CartItem findItem(Cart c, Long bookId) {
		return c.getItems().stream().filter(i -> i.getBook().getId().equals(bookId)).findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Book is not in the cart"));
	}

	private CartResponse empty(String token) {
		return new CartResponse(token, List.of(), 0, BigDecimal.ZERO);
	}

	
	private CartResponse toResponse(Cart c) {
		List<CartItemResponse> items = c.getItems().stream()
				.map(mapper::toItemResponse)
				.toList();
		
		int count = items.stream()
				   .mapToInt(CartItemResponse::quantity)
				   .sum();
		
		BigDecimal subtotal = items.stream()
				.map(CartItemResponse::lineTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		return new CartResponse(c.getCartToken(), items, count, subtotal);
	}
}
