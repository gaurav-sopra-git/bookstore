package com.bookstore.app.services;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.bookstore.app.dtos.BookResponse;

public interface BookService {
	
	Page<BookResponse> getAllBooks(Pageable pageable);

	BookResponse getBookById(Long id);

	List<BookResponse> searchBook(String searchTerm);
}
