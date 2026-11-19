package com.bookstore.app.services.impl;

import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import com.bookstore.app.dtos.BookResponse;
import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.mappers.BookMapper;
import com.bookstore.app.repositories.BookRepository;
import com.bookstore.app.services.BookService;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
	
	private final BookRepository repository;
	private final BookMapper mapper;

	public BookServiceImpl(BookRepository repository, BookMapper mapper) {
		this.repository = repository;
		this.mapper = mapper;
	}

	public Page<BookResponse> getAllBooks(Pageable pageable) {
		return repository.findAll(pageable).map(mapper::toResponse);
	}

	@Cacheable(cacheNames = "bookById", key = "#id")
	public BookResponse getBookById(Long id) {
		return repository.findById(id).map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
	}

	@Cacheable(cacheNames = "bookSearch", key = "#term.trim().toLowerCase(T(java.util.Locale).ROOT)")
	public List<BookResponse> searchBook(String term) {
		if (term == null || term.isBlank())
			throw new IllegalArgumentException("searchText must not be blank");
		return repository.search(term.trim()).stream().map(mapper::toResponse).toList();
	}
}
