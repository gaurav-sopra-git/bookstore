package com.bookstore.app.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"))
@Getter
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 120)
	private String name;
	
	@Column(nullable = false, length = 190)
	private String email;
	
	@Column(nullable = false, length = 100)
	private String password;
	
	@Column(nullable = false, length = 20)
	private String role = "CUSTOMER";
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;


	public static User register(String name, String email, String encodedPassword) {
		var user = new User();
		user.name = name.strip();
		user.email = email.strip().toLowerCase();
		user.password = encodedPassword;
		user.role = "CUSTOMER";
		return user;
	}

	@PrePersist
	void onCreate() {
		createdAt = Instant.now();
	}
}
