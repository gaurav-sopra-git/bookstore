package com.bookstore.app.services;

import java.math.BigDecimal;

public interface PricingService {

	BigDecimal getBookPrice(Long bookId, String currency);
}
