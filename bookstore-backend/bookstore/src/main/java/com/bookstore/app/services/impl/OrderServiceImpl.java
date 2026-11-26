package com.bookstore.app.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.app.dtos.OrderDtos.OrderResponse;
import com.bookstore.app.entities.Book;
import com.bookstore.app.entities.BookPrice;
import com.bookstore.app.entities.Cart;
import com.bookstore.app.entities.CartItem;
import com.bookstore.app.entities.CustomerOrder;
import com.bookstore.app.entities.OrderItem;
import com.bookstore.app.entities.User;
import com.bookstore.app.events.OrderPlacedEvent;
import com.bookstore.app.exceptions.BusinessException;
import com.bookstore.app.exceptions.ResourceNotFoundException;
import com.bookstore.app.mappers.OrderMapper;
import com.bookstore.app.repositories.BookRepository;
import com.bookstore.app.repositories.CartRepository;
import com.bookstore.app.repositories.CustomerOrderRepository;
import com.bookstore.app.repositories.PriceRepository;
import com.bookstore.app.repositories.UserRepository;
import com.bookstore.app.services.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	
	private final CartRepository cartsRepo;
	private final UserRepository usersRepo;
	private final CustomerOrderRepository ordersRepo;
	private final BookRepository booksRepo;
	private final OrderMapper mapper;
	private final ApplicationEventPublisher eventPublisher;
	private final PriceRepository priceRepo;

	public OrderServiceImpl(CartRepository carts, UserRepository users, CustomerOrderRepository orders,
			BookRepository books, OrderMapper mapper, ApplicationEventPublisher eventPublisher, PriceRepository priceRepo) {
		this.cartsRepo = carts;
		this.usersRepo = users;
		this.ordersRepo = orders;
		this.booksRepo = books;
		this.mapper = mapper;
		this.eventPublisher = eventPublisher;
		this.priceRepo = priceRepo;
	}

	@Transactional
	public OrderResponse checkout(Long userId, String address) {
		
		User user = usersRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		
		Cart cart = cartsRepo.findByUserIdWithItems(userId)
				.orElseThrow(() -> new BusinessException("Your cart is empty"));
		
		if (cart.getItems().isEmpty())
			throw new BusinessException("Your cart is empty");
		CustomerOrder order = CustomerOrder.place(user, UUID.randomUUID().toString(), address, "USD");
		
		for (CartItem ci : cart.getItems()) {
			Book book = booksRepo.findById(ci.getBook().getId())
					.orElseThrow(() -> new ResourceNotFoundException("Book no longer exists"));
			if (ci.getQuantity() > book.getStock())
				throw new BusinessException("Insufficient stock for " + book.getTitle());
			book.decreaseStock(ci.getQuantity());
			Optional<BookPrice> bookPrice = priceRepo.findByBookIdAndCurrency(ci.getBook().getId(), "USD");
			OrderItem oi = OrderItem.snapshotOf(book, bookPrice.get().getPrice(), ci.getQuantity());
			order.addItem(oi);
		}
		
		CustomerOrder custOrderEntity = ordersRepo.save(order);
		cart.clearItems();
		eventPublisher.publishEvent(OrderPlacedEvent.from(custOrderEntity.getOrderNumber(), userId, custOrderEntity.getTotalAmount()));
		return mapper.toResponse(custOrderEntity);
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrder(Long userId, String number) {
		return ordersRepo.findDetailedByNumberAndUser(number, userId).map(mapper::toResponse)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrders(Long userId) {
		return ordersRepo.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(mapper::toResponse).toList();
	}
}
