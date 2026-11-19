package com.bookstore.app.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "order_items")
@Getter
public class OrderItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "order_id")
	private CustomerOrder order;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	private Book book;
	
	@Column(nullable = false, length = 200)
	private String bookTitle;
	
	@Column(nullable = false, length = 150)
	private String author;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal unitPrice;
	
	@Column(nullable = false)
	private int quantity;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal lineTotal;


	public static OrderItem snapshotOf(Book book, BigDecimal unitPrice, int quantity) {
		if (book == null) {
			throw new IllegalArgumentException("Book cannot be null");
		}

		if (unitPrice == null || unitPrice.signum() < 0) {
			throw new IllegalArgumentException("Invalid unit price");
		}

		if (quantity < 1) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		var item = new OrderItem();
		item.book = book;
		item.bookTitle = book.getTitle();
		item.author = book.getAuthor();
		item.unitPrice = unitPrice;
		item.quantity = quantity;
		item.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
		return item;
	}

	void attachTo(CustomerOrder order) {
		this.order = order;
	}
}
