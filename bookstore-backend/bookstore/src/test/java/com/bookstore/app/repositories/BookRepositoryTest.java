package com.bookstore.app.repositories;

import static com.bookstore.app.test.helpers.TestData.book;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.bookstore.app.entities.Book;

@DataJpaTest
class BookRepositoryTest {

	@Autowired
	private BookRepository books;

	@BeforeEach
	void seed() {
		books.saveAll(List.of(
				book("Title1", "Name Surname", "499.00", 5),
				book("Title2", "fName sName", "450.00", 2),
				book("Title3", "Name lName", "350.00", 8)));
	}

	@Test
	void search_matchesOnTitleIgnoringCase() {
		assertThat(books.search("TiTLe3"))
				.extracting(Book::getTitle)
				.containsExactly("Title3");
	}


	@Test
	void search_withNoMatch_returnsAnEmptyList() {
		assertThat(books.search("dfggsdgssdfg")).isEmpty();
	}
}
