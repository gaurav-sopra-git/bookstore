package com.bookstore.app.entities;

import java.math.BigDecimal;
import java.util.Currency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "book_prices", 
uniqueConstraints =  @UniqueConstraint( name = "uk_book_currency", columnNames = { "book_id", "currency" })
)
@Getter
public class BookPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@Column(nullable = false, length = 3)
	private String currency;
	
	public static BookPrice create(Book book, BigDecimal price, String currency) {

		if (book == null) {
			throw new IllegalArgumentException("Book cannot be null");
		}

		if (price == null || price.signum() < 0) {
			throw new IllegalArgumentException("Price must be non-negative");
		}

		Currency.getInstance(currency);

		var bookPrice = new BookPrice();
		bookPrice.book = book;
		bookPrice.price = price;
		bookPrice.currency = currency;

		return bookPrice;
	}

}
