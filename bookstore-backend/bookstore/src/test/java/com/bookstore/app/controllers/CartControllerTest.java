package com.bookstore.app.controllers;

import static com.bookstore.app.test.helpers.MvcTestSupport.mockMvcFor;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.bookstore.app.dtos.CartDtos.CartResponse;
import com.bookstore.app.services.CartService;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

	@Mock
	private CartService cartService;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = mockMvcFor(new CartController(cartService));
	}

	@Test
	void addItem_asGuest_usesTheCartTokenHeader() throws Exception {
		when(cartService.addItem(isNull(), eq("tok-1"), eq(10L), eq(2)))
				.thenReturn(new CartResponse("tok-1", List.of(), 2, new BigDecimal("998.00")));

		mvc.perform(post("/api/cart/items")
				.header("X-Cart-Token", "tok-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"bookId": 10, "quantity": 2}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.cartToken").value("tok-1"))
				.andExpect(jsonPath("$.totalItems").value(2));
	}

	@Test
	void addItem_withZeroQuantity_returns400() throws Exception {
		mvc.perform(post("/api/cart/items")
				.header("X-Cart-Token", "tok-1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"bookId": 10, "quantity": 0}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.validationErrors.quantity").exists());

		verifyNoInteractions(cartService);
	}
}
