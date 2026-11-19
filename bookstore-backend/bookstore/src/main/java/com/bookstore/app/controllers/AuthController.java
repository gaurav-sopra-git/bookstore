package com.bookstore.app.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.bookstore.app.dtos.AuthDtos.*;
import com.bookstore.app.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private final AuthService service;

	public AuthController(AuthService service) {
		this.service = service;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.register(request));
	}

	@PostMapping("/login")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return service.login(request);
	}
}
