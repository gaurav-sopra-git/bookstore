package com.bookstore.app.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.app.entities.BookPrice;

public interface PriceRepository extends JpaRepository<BookPrice, Long>{

	Optional<BookPrice> findByBookIdAndCurrency(Long bookId, String currency);
}
