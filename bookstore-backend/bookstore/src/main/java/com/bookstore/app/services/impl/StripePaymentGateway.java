/*
 * package com.bookstore.app.services.impl;
 * 
 * import org.springframework.beans.factory.annotation.Value; import
 * org.springframework.stereotype.Component;
 * 
 * import com.bookstore.app.services.PaymentGateway; import com.stripe.Stripe;
 * import com.stripe.exception.StripeException; import
 * com.stripe.model.checkout.Session; import com.stripe.net.RequestOptions;
 * import com.stripe.param.checkout.SessionCreateParams; import
 * com.stripe.param.checkout.SessionCreateParams.LineItem; import
 * com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData; import
 * com.stripe.param.checkout.SessionCreateParams.LineItem.PriceData.ProductData;
 * 
 * import lombok.extern.slf4j.Slf4j;
 * 
 * @Slf4j
 * 
 * @Component public class StripePaymentGateway implements PaymentGateway {
 * 
 * private final String frontendUrl;
 * 
 * public StripePaymentGateway(@Value("${stripe.secret-key}") String secretKey,
 * 
 * @Value("${app.frontend-url}") String frontendUrl) {
 * 
 * Stripe.apiKey = secretKey; this.frontendUrl = frontendUrl; }
 * 
 * @Override public CheckoutSession createCheckoutSession(CheckoutPaymentRequest
 * request) {
 * 
 * ProductData productData = SessionCreateParams .LineItem .PriceData
 * .ProductData.builder() .setName(request.productName()).build();
 * 
 * PriceData priceData = SessionCreateParams .LineItem .PriceData .builder()
 * .setCurrency(request.currency()) .setUnitAmount(request.amountMinor())
 * .setProductData(productData) .build();
 * 
 * LineItem lineItem = SessionCreateParams .LineItem .builder() .setQuantity(1L)
 * .setPriceData(priceData).build();
 * 
 * SessionCreateParams params =
 * SessionCreateParams.builder().setMode(SessionCreateParams.Mode.PAYMENT)
 * .setSuccessUrl(frontendUrl +
 * "/payment/success?session_id={CHECKOUT_SESSION_ID}")
 * .setCancelUrl(frontendUrl + "/payment/cancel?orderNumber=" +
 * request.orderNumber()) .addLineItem(lineItem).build();
 * 
 * try { RequestOptions options =
 * RequestOptions.builder().setIdempotencyKey(request.idempotencyKey()).build();
 * 
 * Session session = Session.create(params, options);
 * 
 * return new CheckoutSession(session.getId(), session.getUrl());
 * 
 * } catch (StripeException ex) {
 * log.error("Stripe Checkout Session creation failed for order {}",
 * request.orderNumber(), ex);
 * 
 * throw new
 * IllegalStateException("Unable to initiate payment. Please try again.", ex); }
 * } }
 */