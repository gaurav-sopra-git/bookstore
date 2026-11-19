package com.bookstore.app.services;

import com.bookstore.app.dtos.AuthDtos.*;

public interface AuthService {
	
	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);
}
