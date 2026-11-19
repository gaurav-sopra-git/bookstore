package com.bookstore.app.test.helpers;

import java.util.List;

import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bookstore.app.exceptions.GlobalExceptionHandler;
import com.bookstore.app.security.AuthenticatedUser;

public final class MvcTestSupport {

	private MvcTestSupport() {
	}

	public static MockMvc mockMvcFor(Object controller) {
		return MockMvcBuilders.standaloneSetup(controller)
				.setControllerAdvice(new GlobalExceptionHandler())
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.build();
	}

	public static Authentication loggedInAs(long userId) {
		var principal = new AuthenticatedUser(userId, "user" + userId + "@example.com", "CUSTOMER");
		return new UsernamePasswordAuthenticationToken(principal, null, List.of());
	}
}
