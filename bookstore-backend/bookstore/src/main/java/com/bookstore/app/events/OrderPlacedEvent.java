package com.bookstore.app.events;

import java.math.BigDecimal;
import java.time.Instant;


public record OrderPlacedEvent(String orderNumber, Long customerId, BigDecimal totalAmount, Instant occurredAt) {
	
	public static OrderPlacedEvent from(String orderNumber, Long customerId, BigDecimal totalAmount) {
		return new OrderPlacedEvent(orderNumber, customerId, totalAmount, Instant.now());
	}
}
