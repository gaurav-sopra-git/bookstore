package com.bookstore.app.dtos;

import java.math.BigDecimal;

public record BookResponse(Long id, String title, String author, String description, int stock, BigDecimal price,
		String imageUrl) {
}
