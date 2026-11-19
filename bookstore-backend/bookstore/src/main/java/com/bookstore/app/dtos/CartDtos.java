package com.bookstore.app.dtos;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public final class CartDtos {
	
	private CartDtos() {
	}

	public record AddItemRequest(@NotNull Long bookId, @Min(1) int quantity) {
	}

	public record UpdateQuantityRequest(@Min(1) int quantity) {
	}

	public record BookSummary(Long id, String title, String author, String imageUrl) {
	}

	public record CartItemResponse(Long id, BookSummary book, int quantity, BigDecimal unitPrice,
			BigDecimal lineTotal) {
	}

	public record CartResponse(String cartToken, List<CartItemResponse> items, int totalItems, BigDecimal subtotal) {
	}
}
