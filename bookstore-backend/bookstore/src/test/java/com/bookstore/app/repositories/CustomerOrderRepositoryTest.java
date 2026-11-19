package com.bookstore.app.repositories;

import static com.bookstore.app.test.helpers.TestData.book;
import static com.bookstore.app.test.helpers.TestData.user;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.bookstore.app.entities.Book;
import com.bookstore.app.entities.CustomerOrder;
import com.bookstore.app.entities.OrderItem;
import com.bookstore.app.entities.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest
class CustomerOrderRepositoryTest {

	@Autowired
	private CustomerOrderRepository orders;
	@Autowired
	private UserRepository users;
	@Autowired
	private BookRepository books;

	@PersistenceContext
	private EntityManager em;

	private User asha;
	private User ravi;
	private Book book;

	@BeforeEach
	void setUp() {
		asha = users.save(user("asha@example.com"));
		ravi = users.save(user("ravi@example.com"));
		book = books.save(book("Title", "name surname", "499.00", 5));
	}

	@Test
	void findDetailedByNumberAndUser_returnsTheOrderWithItsItems() {
		CustomerOrder order = CustomerOrder.place(asha, "ORD-1", "12 Park Street", "INR");
		order.addItem(OrderItem.snapshotOf(book, new BigDecimal("499.00"), 2));
		orders.saveAndFlush(order);
		em.clear();

		CustomerOrder found = orders.findDetailedByNumberAndUser("ORD-1", asha.getId()).orElseThrow();

		assertThat(found.getTotalAmount()).isEqualByComparingTo("998.00");
		assertThat(found.getItems()).hasSize(1);
		assertThat(found.getItems().get(0).getBookTitle()).isEqualTo("Title");
	}

	@Test
	void findDetailedByNumberAndUser_doesNotLeakOrdersToOtherUsers() {
		orders.saveAndFlush(CustomerOrder.place(asha, "ORD-1", "12 Park Street", "INR"));

		assertThat(orders.findDetailedByNumberAndUser("ORD-1", ravi.getId())).isEmpty();
		assertThat(orders.findByOrderNumberAndUser_Id("ORD-1", ravi.getId())).isEmpty();
	}

}
