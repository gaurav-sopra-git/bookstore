package com.bookstore.app.services;

import com.bookstore.app.dtos.CartDtos.CartResponse;

public interface CartService {
	
	CartResponse createGuestCart();

	CartResponse getCart(Long userId, String guestToken);

	CartResponse addItem(Long userId, String guestToken, Long bookId, int quantity);

	CartResponse updateQuantity(Long userId, String guestToken, Long bookId, int quantity);

	CartResponse removeItem(Long userId, String guestToken, Long bookId);

	void clear(Long userId);
}
