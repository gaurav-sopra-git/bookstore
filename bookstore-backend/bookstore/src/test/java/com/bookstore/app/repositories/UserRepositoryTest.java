package com.bookstore.app.repositories;

import static com.bookstore.app.test.helpers.TestData.user;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private UserRepository users;

	@Test
	void findByEmail_andExistsByEmail_workForASavedUser() {
		users.save(user("asha@example.com"));

		assertThat(users.findByEmail("asha@example.com")).isPresent();
		assertThat(users.existsByEmail("asha@example.com")).isTrue();
		assertThat(users.existsByEmail("ravi@example.com")).isFalse();
	}

	@Test
	void emailMustBeUnique() {
		users.save(user("asha@example.com"));

		assertThatThrownBy(() -> users.saveAndFlush(user("asha@example.com")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}
}
