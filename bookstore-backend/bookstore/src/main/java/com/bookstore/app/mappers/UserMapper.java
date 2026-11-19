package com.bookstore.app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.bookstore.app.entities.User;
import com.bookstore.app.dtos.AuthDtos.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
	
	@Mapping(target = "role", source = "role")
	UserResponse toResponse(User user);
}
