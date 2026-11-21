package com.bookstore.app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.bookstore.app.dtos.OrderDtos.OrderItemResponse;
import com.bookstore.app.dtos.OrderDtos.OrderResponse;
import com.bookstore.app.entities.CustomerOrder;
import com.bookstore.app.entities.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {
	
	@Mapping(target = "status", expression = "java(order.getStatus().name())")
	OrderResponse toResponse(CustomerOrder order);

	OrderItemResponse toItemResponse(OrderItem item);
}
