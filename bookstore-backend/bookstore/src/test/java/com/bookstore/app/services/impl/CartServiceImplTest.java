package com.bookstore.app.services.impl;

import static com.bookstore.app.test.helpers.TestData.book;
import static com.bookstore.app.test.helpers.TestData.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.app.dtos.CartDtos.CartResponse;
import com.bookstore.app.entities.Book;
import com.bookstore.app.entities.Cart;
import com.bookstore.app.entities.CartItem;
import com.bookstore.app.mappers.CartMapper;
import com.bookstore.app.repositories.BookRepository;
import com.bookstore.app.repositories.CartRepository;
import com.bookstore.app.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

	@Mock
	private CartRepository cartRepo;
	@Mock
	private BookRepository bookRepo;
	@Mock
	private UserRepository userRepo;

	
	private CartServiceImpl service;
	private Book bookEntity;

	@BeforeEach
	void setUp() {
		service = new CartServiceImpl(cartRepo, bookRepo, userRepo, Mappers.getMapper(CartMapper.class));
		bookEntity = withId(book("Title", "Name Surname", "499.00", 5), 10L);
	}

	@Test
	void addItem_toNewGuestCart_createsTheCartAndTheLine() {
		when(cartRepo.findByTokenWithItems("tok-1")).thenReturn(Optional.empty());
		when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));
		when(bookRepo.findById(10L)).thenReturn(Optional.of(bookEntity));

		CartResponse response = service.addItem(null, "tok-1", 10L, 2);

		assertThat(response.cartToken()).isEqualTo("tok-1");
		assertThat(response.totalItems()).isEqualTo(2);
		assertThat(response.subtotal()).isEqualByComparingTo("998.00");
		assertThat(response.items()).hasSize(1);
		assertThat(response.items().get(0).book().title()).isEqualTo("Title");
	}

	@Test
	void addItem_forABookAlreadyInTheCart_addsToTheQuantity() {
		Cart cart = cartWith(bookEntity, 1);
		when(cartRepo.findByTokenWithItems("tok-1")).thenReturn(Optional.of(cart));
		when(bookRepo.findById(10L)).thenReturn(Optional.of(bookEntity));
		when(cartRepo.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

		CartResponse response = service.addItem(null, "tok-1", 10L, 2);

		assertThat(response.items()).hasSize(1);
		assertThat(response.totalItems()).isEqualTo(3);
	}


	private Cart cartWith(Book book, int quantity) {
		Cart cart = Cart.guest("tok-1");
		cart.addItem(CartItem.forBook(book, quantity));
		return cart;
	}
}
