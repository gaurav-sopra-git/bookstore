package com.bookstore.app.controllers;

import static com.bookstore.app.test.helpers.MvcTestSupport.loggedInAs;
import static com.bookstore.app.test.helpers.MvcTestSupport.mockMvcFor;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.bookstore.app.dtos.CheckoutSessionResponse;
import com.bookstore.app.services.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

	@Mock
	private PaymentService paymentService;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = mockMvcFor(new PaymentController(paymentService));
	}

	@Test
	void initiateCheckout_returnsTheStripeUrl() throws Exception {
		when(paymentService.initiateCheckout(7L, "ORD-1", "STRIPE")).thenReturn(
				new CheckoutSessionResponse("ORD-1", "https://checkout.stripe.com/c/pay/cs_test_1", "CREATED"));

		mvc.perform(post("/api/payments/ORD-1/checkout-session").principal(loggedInAs(7)))
				.andExpect(status().is5xxServerError())
				.andExpect(jsonPath("$.checkoutUrl").value("https://checkout.stripe.com/c/pay/cs_test_1"))
				.andExpect(jsonPath("$.paymentStatus").value("CREATED"));
	}

}
