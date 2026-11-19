package com.bookstore.app.controllers;

import static com.bookstore.app.test.helpers.MvcTestSupport.loggedInAs;
import static com.bookstore.app.test.helpers.MvcTestSupport.mockMvcFor;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bookstore.app.dtos.OrderDtos.OrderResponse;
import com.bookstore.app.services.OrderService;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

	@Mock
	private OrderService orderService;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = mockMvcFor(new CheckoutController(orderService));
	}

	@Test
	void checkout_createsOrderForTheSignedInUser() throws Exception {
		var order = new OrderResponse("ORD-1", "PENDING_PAYMENT", new BigDecimal("998.00"),
				"12 Park Street, Kolkata", Instant.parse("2026-11-15T10:00:00Z"), List.of());
		when(orderService.checkout(7L, "12 Park Street, Kolkata")).thenReturn(order);

		mvc.perform(post("/api/checkout")
				.principal(loggedInAs(7))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"shippingAddress": "12 Park Street, Kolkata"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.orderNumber").value("ORD-1"))
				.andExpect(jsonPath("$.status").value("PENDING_PAYMENT"));
	}

	@Test
	void checkout_withBlankAddress_returns400() throws Exception {
		mvc.perform(post("/api/checkout")
				.principal(loggedInAs(7))
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"shippingAddress": "  "}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.validationErrors.shippingAddress").exists());

		verifyNoInteractions(orderService);
	}
}
