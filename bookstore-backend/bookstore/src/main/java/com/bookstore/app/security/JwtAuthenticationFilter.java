package com.bookstore.app.security;

import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				Claims claims = jwtService.parse(header.substring(7));
				Number idClaim = (Number) claims.get("uid");
				Long id = idClaim.longValue();
				String email = claims.getSubject();
				String role = claims.get("role", String.class);
				var auth = new UsernamePasswordAuthenticationToken(new AuthenticatedUser(id, email, role), null,
						List.of(new SimpleGrantedAuthority("ROLE_" + role)));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (RuntimeException ignored) {
				SecurityContextHolder.clearContext();
			}
		}
		chain.doFilter(request, response);
	}
}
