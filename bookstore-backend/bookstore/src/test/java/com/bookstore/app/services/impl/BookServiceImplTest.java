package com.bookstore.app.services.impl;

import static com.bookstore.app.test.helpers.TestData.book;
import static com.bookstore.app.test.helpers.TestData.withId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.app.dtos.BookResponse;
import com.bookstore.app.entities.Book;
import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.mappers.BookMapper;
import com.bookstore.app.repositories.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

	@Mock
	private BookRepository repository;

	private BookServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new BookServiceImpl(repository, Mappers.getMapper(BookMapper.class));
	}

	@Test
	void getBookById_returnsMappedBook() {
		Book bookEntity = withId(book("Title", "Name Surname", "499.00", 5), 1L);
		when(repository.findById(1L)).thenReturn(Optional.of(bookEntity));

		BookResponse response = service.getBookById(1L);

		assertThat(response.id()).isEqualTo(1L);
		assertThat(response.title()).isEqualTo("Title");
		assertThat(response.stock()).isEqualTo(5);
	}

	@Test
	void getBookById_whenMissing_throwsNotFound() {
		when(repository.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getBookById(99L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("Book not found: 99");
	}


	@Test
	void searchBook_withBlankTerm_isRejectedWithoutQuerying() {
		assertThatThrownBy(() -> service.searchBook("   "))
				.isInstanceOf(IllegalArgumentException.class);

		verifyNoInteractions(repository);
	}

}
