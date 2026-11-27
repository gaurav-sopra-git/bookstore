package com.bookstore.app.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey key;
	private final long expirationMs;

	public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-ms}") long expirationMs) {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		if (keyBytes.length < 32)
			throw new IllegalArgumentException("JWT secret must contain at least 32 bytes");
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.expirationMs = expirationMs;
	}

	public String generateToken(Long userId, String email, String role) {
		Date now = new Date();
		return Jwts.builder()
				.subject(email)
				.claim("uid", userId)
				.claim("role", role)
				.issuedAt(now)
				.expiration(new Date(now.getTime() + expirationMs)).signWith(key)
				.compact();
	}

	public Claims parse(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public long getExpirationMs() {
		return expirationMs;
	}
}
