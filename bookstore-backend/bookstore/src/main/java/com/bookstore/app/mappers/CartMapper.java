package com.bookstore.app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookstore.app.dtos.CartDtos.BookSummary;
import com.bookstore.app.dtos.CartDtos.CartItemResponse;
import com.bookstore.app.entities.Book;
import com.bookstore.app.entities.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {
	@Mapping(target = "book", source = "book")
	@Mapping(target = "unitPrice", source = "book.price")
	@Mapping(target = "lineTotal", expression = "java(item.getBook().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))")
	CartItemResponse toItemResponse(CartItem item);

	BookSummary toBookSummary(Book book);
}
