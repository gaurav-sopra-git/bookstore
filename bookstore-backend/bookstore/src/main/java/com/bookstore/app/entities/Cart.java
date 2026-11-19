package com.bookstore.app.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "carts")
@Getter
public class Cart {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, length = 36)
	private String cartToken;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", unique = true)
	private User user;
	
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> items = new ArrayList<>();


	/** Expose a read-only snapshot; mutations go through domain methods. */
	public List<CartItem> getItems() {
		return List.copyOf(items);
	}

	public static Cart guest(String token) {
		var cart = new Cart();
		cart.cartToken = token;
		return cart;
	}

	public static Cart forUser(User user) {
		var cart = new Cart();
		cart.user = user;
		return cart;
	}

	public void addItem(CartItem item) {
		items.add(item);
		item.attachTo(this);
	}

	public void removeItem(CartItem item) {
		items.remove(item);
		item.detach();
	}

	public void clearItems() {
		items.clear();
	}
}
