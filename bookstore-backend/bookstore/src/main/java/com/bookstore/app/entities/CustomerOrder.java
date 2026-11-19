package com.bookstore.app.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.bookstore.app.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "customer_orders")
@Getter
public class CustomerOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, unique = true, length = 36)
	private String orderNumber;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalAmount;
	
	@Column(nullable = false, length = 300)
	private String shippingAddress;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> items = new ArrayList<>();

	@Column(nullable = false, length = 3)
	private String currency;
	

	/** Expose a read-only snapshot; mutations go through domain methods. */
	public List<OrderItem> getItems() {
		return List.copyOf(items);
	}

	public static CustomerOrder place(User user, String orderNumber, String shippingAddress, String currency) {
		var order = new CustomerOrder();
		order.user = user;
		order.orderNumber = orderNumber;
		order.shippingAddress = shippingAddress.strip();
		order.status = OrderStatus.PENDING_PAYMENT;
		order.totalAmount = BigDecimal.ZERO;
		order.currency = currency;
		return order;
	}

	public void addItem(OrderItem item) {
		items.add(item);
		item.attachTo(this);
		totalAmount = totalAmount.add(item.getLineTotal());
	}

	
	public void confirm() {
		if (status != OrderStatus.PENDING_PAYMENT) {
			throw new IllegalStateException("Only pending-payment orders can be confirmed");
		}

		this.status = OrderStatus.CONFIRMED;
	}

	public void cancel() {
		if (status != OrderStatus.PENDING_PAYMENT) {
			throw new IllegalStateException("Only pending-payment orders can be cancelled");
		}

		this.status = OrderStatus.CANCELLED;
	}
	
	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}
}
