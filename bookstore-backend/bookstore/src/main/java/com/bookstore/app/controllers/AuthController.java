package com.bookstore.app.controllers;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.app.dtos.AuthDtos.AuthResponse;
import com.bookstore.app.dtos.AuthDtos.AuthResult;
import com.bookstore.app.dtos.AuthDtos.LoginRequest;
import com.bookstore.app.dtos.AuthDtos.RegisterRequest;
import com.bookstore.app.services.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private static final String ACCESS_TOKEN_COOKIE = "BOOKSTORE_ACCESS_TOKEN";
	
	private final AuthService service;
	private final boolean secureCookie;

	public AuthController(AuthService service, 
			@Value("${app.security.cookie-secure:false}") boolean secureCookie) {
		this.service = service;
		this.secureCookie = secureCookie;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return authResponse(service.register(request), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return authResponse(service.login(request), HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, "")
				.httpOnly(true)
				.secure(secureCookie)
				.sameSite("Lax")
				.path("/")
				.maxAge(Duration.ZERO)
				.build();
		return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
	}

	@GetMapping("/csrf")
	public Map<String, String> csrf(CsrfToken csrfToken) {
		return Map.of("token", csrfToken.getToken());
	}
	
	private ResponseEntity<AuthResponse> authResponse(AuthResult result, HttpStatus status) {
		ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE, result.accessToken())
				.httpOnly(true).secure(secureCookie).sameSite("Lax").path("/")
				.maxAge(Duration.ofSeconds(result.expiresIn())).build();
		return ResponseEntity.status(status).header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new AuthResponse(result.user()));
	}
}
