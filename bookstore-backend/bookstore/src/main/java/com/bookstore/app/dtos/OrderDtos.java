package com.bookstore.app.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class OrderDtos {
	
	private OrderDtos() {
	}

	public record CheckoutRequest(@NotBlank @Size(max = 300) String shippingAddress) {
	}

	public record OrderItemResponse(String bookTitle, String author, BigDecimal unitPrice, int quantity,
			BigDecimal lineTotal) {
	}

	public record OrderResponse(String orderNumber, String status, BigDecimal totalAmount, String shippingAddress,
			Instant createdAt, List<OrderItemResponse> items) {
	}
}
