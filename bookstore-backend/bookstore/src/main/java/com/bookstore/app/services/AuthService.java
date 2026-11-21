package com.bookstore.app.services;

import com.bookstore.app.dtos.AuthDtos.AuthResult;
import com.bookstore.app.dtos.AuthDtos.LoginRequest;
import com.bookstore.app.dtos.AuthDtos.RegisterRequest;

public interface AuthService {
	
	AuthResult register(RegisterRequest request);

	AuthResult login(LoginRequest request);
}
