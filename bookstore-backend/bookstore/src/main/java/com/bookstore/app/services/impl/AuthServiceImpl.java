package com.bookstore.app.services.impl;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.app.dtos.AuthDtos.AuthResult;
import com.bookstore.app.dtos.AuthDtos.LoginRequest;
import com.bookstore.app.dtos.AuthDtos.RegisterRequest;
import com.bookstore.app.entities.User;
import com.bookstore.app.exceptions.BusinessException;
import com.bookstore.app.mappers.UserMapper;
import com.bookstore.app.repositories.UserRepository;
import com.bookstore.app.security.JwtService;
import com.bookstore.app.services.AuthService;

@Service
public class AuthServiceImpl implements AuthService {
	
	private final UserRepository userRepo;
	private final PasswordEncoder encoder;
	private final JwtService jwt;
	private final UserMapper mapper;

	public AuthServiceImpl(UserRepository userRepo, PasswordEncoder encoder, JwtService jwt, UserMapper mapper) {
		this.userRepo = userRepo;
		this.encoder = encoder;
		this.jwt = jwt;
		this.mapper = mapper;
	}

	@Transactional
	public AuthResult register(RegisterRequest request) {
		String email = request.email().trim().toLowerCase();
		if (userRepo.existsByEmail(email))
			throw new BusinessException("An account with this email already exists");
		User user = User.register(request.name(), email, encoder.encode(request.password()));
		User userEntity = userRepo.save(user);
		return response(userEntity);
	}

	@Transactional(readOnly = true)
	public AuthResult login(LoginRequest r) {
		User u = userRepo.findByEmail(r.email().trim().toLowerCase())
				.orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
		if (!encoder.matches(r.password(), u.getPassword()))
			throw new BadCredentialsException("Invalid credentials");
		return response(u);
	}

	private AuthResult response(User u) {
		return new AuthResult(jwt.generateToken(u.getId(), u.getEmail(), u.getRole()),
				jwt.getExpirationMs() / 1000, mapper.toResponse(u));
	}
}
