package com.bookstore.app.test.helpers;

import java.math.BigDecimal;

import org.springframework.test.util.ReflectionTestUtils;

import com.bookstore.app.entities.Book;
import com.bookstore.app.entities.User;

public final class TestData {

	private TestData() {
	}

	public static Book book(String title, String author, String price, int stock) {
		return Book.create(title, author, "A book called " + title, new BigDecimal(price), stock, null);
	}

	public static User user(String email) {
		return User.register("Test User", email, "hashed-password");
	}

	public static <T> T withId(T entity, Long id) {
		ReflectionTestUtils.setField(entity, "id", id);
		return entity;
	}
}
