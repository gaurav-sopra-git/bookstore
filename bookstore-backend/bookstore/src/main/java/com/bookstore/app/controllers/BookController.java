package com.bookstore.app.controllers;

import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import com.bookstore.app.dtos.BookResponse;
import com.bookstore.app.services.BookService;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	private final BookService service;

	public BookController(BookService service) {
		this.service = service;
	}

	@GetMapping
	public Page<BookResponse> getAllBooks(@PageableDefault(size = 20, sort = "title") Pageable pageable) {
		return service.getAllBooks(pageable);
	}

	@GetMapping("/search")
	public List<BookResponse> search(@RequestParam String searchText) {
		return service.searchBook(searchText);
	}

	@GetMapping("/{id}")
	public BookResponse getById(@PathVariable Long id) {
		return service.getBookById(id);
	}
}
