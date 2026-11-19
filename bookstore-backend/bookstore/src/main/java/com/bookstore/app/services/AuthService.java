package com.bookstore.app.services;

import com.bookstore.app.dtos.AuthDtos.*;

public interface AuthService {
	
	AuthResult register(RegisterRequest request);

	AuthResult login(LoginRequest request);
}
