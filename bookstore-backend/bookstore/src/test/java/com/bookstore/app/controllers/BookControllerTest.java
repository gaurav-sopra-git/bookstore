package com.bookstore.app.controllers;

import static com.bookstore.app.test.helpers.MvcTestSupport.mockMvcFor;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.bookstore.app.dtos.BookResponse;
import com.bookstore.app.services.BookService;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

	@Mock
	private BookService bookService;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = mockMvcFor(new BookController(bookService));
	}


	@Test
	void getById_returnsTheBook() throws Exception {
		when(bookService.getBookById(1L)).thenReturn(Title());

		mvc.perform(get("/api/books/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.author").value("Fname Lname"));
	}


	@Test
	void search_passesTheSearchTermToTheService() throws Exception {
		when(bookService.searchBook("Title")).thenReturn(List.of(Title()));

		mvc.perform(get("/api/books/search").param("searchText", "Title"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].title").value("Title"));
	}

	private BookResponse Title() {
		return new BookResponse(1L, "Title", "Fname Lname", "Test Desc", 5, new BigDecimal("499.00"), null);
	}
}
