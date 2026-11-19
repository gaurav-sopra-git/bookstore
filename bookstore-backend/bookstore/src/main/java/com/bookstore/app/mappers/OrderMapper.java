package com.bookstore.app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.bookstore.app.entities.*;
import com.bookstore.app.dtos.OrderDtos.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	
	@Mapping(target = "status", expression = "java(order.getStatus().name())")
	OrderResponse toResponse(CustomerOrder order);

	OrderItemResponse toItemResponse(OrderItem item);
}
