package com.bookstore.app.controllers;

import static com.bookstore.app.test.helpers.MvcTestSupport.mockMvcFor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import com.bookstore.app.dtos.AuthDtos.AuthResponse;
import com.bookstore.app.dtos.AuthDtos.LoginRequest;
import com.bookstore.app.dtos.AuthDtos.RegisterRequest;
import com.bookstore.app.dtos.AuthDtos.UserResponse;
import com.bookstore.app.services.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private AuthService authService;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = mockMvcFor(new AuthController(authService));
	}

	@Test
	void register_returns201WithToken() throws Exception {
		var user = new UserResponse(1L, "Name Surname", "gaurav@example.com", "CUSTOMER");
		when(authService.register(any(RegisterRequest.class)))
				.thenReturn(new AuthResponse("jwt-token", "Bearer", 86400, user));

		mvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"name": "Name Surname", "email": "gaurav@example.com", "password": "supersecret1"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.accessToken").value("jwt-token"))
				.andExpect(jsonPath("$.user.email").value("gaurav@example.com"));
	}


	@Test
	void login_withBadCredentials_returns401() throws Exception {
		when(authService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

		mvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "gaurav@example.com", "password": "wrong-password"}
						"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Invalid email or password"));
	}
}
