package com.bookstore.app.controllers;

import static com.bookstore.app.test.helpers.MvcTestSupport.mockMvcFor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import com.bookstore.app.dtos.AuthDtos.AuthResult;
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
		mvc = mockMvcFor(new AuthController(authService, false));
	}

	@Test
	void register_returns201AndSetsHttpOnlyCookie() throws Exception {
		var user = new UserResponse(1L, "Name Surname", "gaurav@example.com", "CUSTOMER");
		when(authService.register(any(RegisterRequest.class))).thenReturn(new AuthResult("jwt-token", 86400, user));

		mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
				{"name": "Name Surname", "email": "gaurav@example.com", "password": "supersecret1"}
				""")).andExpect(status().isCreated())
				.andExpect(header().string("Set-Cookie",
						org.hamcrest.Matchers.containsString("BOOKSTORE_ACCESS_TOKEN=jwt-token")))
				.andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
				.andExpect(jsonPath("$.accessToken").doesNotExist())
				.andExpect(jsonPath("$.user.email").value("gaurav@example.com"));
	}

	@Test
	void login_withBadCredentials_returns401() throws Exception {
		when(authService.login(any(LoginRequest.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

		mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
				{"email": "gaurav@example.com", "password": "wrong-password"}
				""")).andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.message").value("Invalid email or password"));
	}
}
