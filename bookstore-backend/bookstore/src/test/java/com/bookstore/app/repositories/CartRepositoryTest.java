package com.bookstore.app.repositories;

import static com.bookstore.app.test.helpers.TestData.book;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.bookstore.app.entities.Book;
import com.bookstore.app.entities.Cart;
import com.bookstore.app.entities.CartItem;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class CartRepositoryTest {

	@Autowired
	private CartRepository carts;
	@Autowired
	private BookRepository books;

	@PersistenceContext
	private EntityManager em;

	@Test
	void findByTokenWithItems_fetchesTheItemsAndTheirBooks() {
		Book dune = books.save(book("Title", "name surname", "499.00", 5));
		Cart cart = Cart.guest("tok-1");
		cart.addItem(CartItem.forBook(dune, 2));
		carts.saveAndFlush(cart);
		em.clear();
		Cart loaded = carts.findByTokenWithItems("tok-1").orElseThrow();

		assertThat(em.getEntityManagerFactory().getPersistenceUnitUtil().isLoaded(loaded, "items")).isTrue();
		assertThat(loaded.getItems()).hasSize(1);
		assertThat(loaded.getItems().get(0).getQuantity()).isEqualTo(2);
		assertThat(loaded.getItems().get(0).getBook().getTitle()).isEqualTo("Title");
	}

	@Test
	void findByTokenWithItems_withUnknownToken_returnsEmpty() {
		assertThat(carts.findByTokenWithItems("does-not-exist")).isEmpty();
	}
}
