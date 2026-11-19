package com.bookstore.app.services;

import java.util.List;
import com.bookstore.app.dtos.OrderDtos.*;


public interface OrderService {
	
	OrderResponse checkout(Long userId, String shippingAddress);

	OrderResponse getOrder(Long userId, String orderNumber);

	List<OrderResponse> getOrders(Long userId);
}
