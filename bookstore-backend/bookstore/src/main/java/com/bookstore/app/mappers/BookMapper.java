package com.bookstore.app.mappers;

import org.mapstruct.Mapper;
import com.bookstore.app.entities.Book;
import com.bookstore.app.dtos.BookResponse;

@Mapper(componentModel = "spring")
public interface BookMapper {
	
	BookResponse toResponse(Book book);
}
