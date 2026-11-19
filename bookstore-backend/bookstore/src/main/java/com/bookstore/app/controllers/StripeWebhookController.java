package com.bookstore.app.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.services.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class StripeWebhookController {

	private final PaymentService paymentService;
	private final String webhookSecret;
	private final ObjectMapper objectMapper;

	public StripeWebhookController(PaymentService paymentService,
			@Value("${stripe.webhook-secret}") String webhookSecret, ObjectMapper objectMapper) {
		this.paymentService = paymentService;
		this.webhookSecret = webhookSecret;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/webhook")
	public ResponseEntity<String> handleWebhook(@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String signature) throws JsonMappingException, JsonProcessingException {
		log.info("Stripe webhook request received");

		final Event event;

		try {
			event = Webhook.constructEvent(payload, signature, webhookSecret);
			log.info("Verified Stripe event: id={}, type={}", event.getId(), event.getType());
		} catch (SignatureVerificationException ex) {
			 log.error("Stripe signature verification failed: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe signature");
		} catch (RuntimeException ex) {
			log.error("Failed to construct Stripe webhook event", ex);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid webhook payload");
		}

		JsonNode root = objectMapper.readTree(payload);

		JsonNode session = root.path("data").path("object");

		String sessionId = session.path("id").asText();
		String paymentIntentId = session.path("payment_intent").asText(null);
		Long amountTotal = session.path("amount_total").asLong();
		String currency = session.path("currency").asText();
		String paymentStatus = session.path("payment_status").asText();

		log.info("Stripe event type: {}", event.getType());
		log.info("Event API version: {}", event.getApiVersion());
		log.info("SDK API version: {}", Stripe.API_VERSION);
	
		log.info("Processing completed Stripe session: {}", sessionId);

		switch (event.getType()) {
		
		case "checkout.session.completed", "checkout.session.async_payment_succeeded" ->
			
			paymentService.handleCheckoutCompleted(sessionId, paymentIntentId, amountTotal, currency, paymentStatus);

		case "checkout.session.async_payment_failed" -> paymentService.handleAsyncPaymentFailed(sessionId);

		default -> {
			//
			}
		}

		return ResponseEntity.ok("Webhook processed");
	}
}