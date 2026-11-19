package com.bookstore.app.services.impl;

import static com.bookstore.app.test.helpers.TestData.user;
import static com.bookstore.app.test.helpers.TestData.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.bookstore.app.dtos.OrderDtos.OrderResponse;
import com.bookstore.app.entities.Cart;
import com.bookstore.app.entities.CustomerOrder;
import com.bookstore.app.entities.User;
import com.bookstore.app.exceptions.BusinessException;
import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.mappers.OrderMapper;
import com.bookstore.app.repositories.BookRepository;
import com.bookstore.app.repositories.CartRepository;
import com.bookstore.app.repositories.CustomerOrderRepository;
import com.bookstore.app.repositories.PriceRepository;
import com.bookstore.app.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private CartRepository carts;
	@Mock
	private UserRepository users;
	@Mock
	private CustomerOrderRepository orders;
	@Mock
	private BookRepository books;
	@Mock
	private PriceRepository prices;
	@Mock
	private ApplicationEventPublisher events;

	private OrderServiceImpl service;
	private User user;

	@BeforeEach
	void setUp() {
		service = new OrderServiceImpl(carts, users, orders, books, Mappers.getMapper(OrderMapper.class), events,
				prices);
		user = withId(user("user@example.com"), 1L);
	}

	@Test
	void checkout_withAnEmptyCart_isRejected() {
		when(users.findById(1L)).thenReturn(Optional.of(user));
		when(carts.findByUserIdWithItems(1L)).thenReturn(Optional.of(Cart.forUser(user)));

		assertThatThrownBy(() -> service.checkout(1L, "12 Park Street"))
				.isInstanceOf(BusinessException.class)
				.hasMessage("Your cart is empty");

		verify(orders, never()).save(any());
	}

	@Test
	void getOrder_forAnotherUsersOrder_throwsNotFound() {
		when(orders.findDetailedByNumberAndUser("ORD-1", 2L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getOrder(2L, "ORD-1"))
				.isInstanceOf(ResourceNotFoundException.class);
	}

	@Test
	void getOrders_returnsTheUsersOrders() {
		CustomerOrder order = CustomerOrder.place(user, "ORD-1", "12 Park Street", "INR");
		when(orders.findAllByUserIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(order));

		List<OrderResponse> result = service.getOrders(1L);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).orderNumber()).isEqualTo("ORD-1");
	}
}
