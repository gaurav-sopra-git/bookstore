package com.bookstore.app.services.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.repositories.PriceRepository;
import com.bookstore.app.services.PricingService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PriceServiceImpl implements PricingService {
	
	private PriceRepository priceRepository;

	
	public PriceServiceImpl(PriceRepository priceRepository) {
		this.priceRepository = priceRepository;
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal getBookPrice(Long bookId, String currency) {
		
		return priceRepository.findByBookIdAndCurrency(bookId, currency)
				.orElseThrow(() -> new ResourceNotFoundException("Price not found"))
				.getPrice();
				
	}

}
