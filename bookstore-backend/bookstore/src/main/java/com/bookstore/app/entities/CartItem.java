package com.bookstore.app.entities;

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
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(name = "uk_cart_book", columnNames = { "cart_id",
		"book_id" }))
@Getter
public class CartItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;
	
	@Column(nullable = false)
	private int quantity;


	public static CartItem forBook(Book book, int quantity) {
		if (quantity < 1)
			throw new IllegalArgumentException("Quantity must be positive");
		var item = new CartItem();
		item.book = book;
		item.quantity = quantity;
		return item;
	}

	void attachTo(Cart cart) {
		this.cart = cart;
	}

	void detach() {
		this.cart = null;
	}

	public void changeQuantity(int quantity) {
		if (quantity < 1)
			throw new IllegalArgumentException("Quantity must be positive");
		this.quantity = quantity;
	}

	public void increaseBy(int quantity) {
		changeQuantity(Math.addExact(this.quantity, quantity));
	}
}
