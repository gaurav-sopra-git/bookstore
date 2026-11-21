package com.bookstore.app.exceptions;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	public record ApiError(Instant timestamp, int status, String error, String message,
			Map<String, String> validationErrors, String path) {
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<ApiError> notFound(ResourceNotFoundException ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of(), req);
	}

	@ExceptionHandler({ BusinessException.class, IllegalArgumentException.class })
	ResponseEntity<ApiError> badRequest(RuntimeException ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.BAD_REQUEST, ex.getMessage(), Map.of(), req);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex,
			jakarta.servlet.http.HttpServletRequest req) {
		Map<String, String> errors = new LinkedHashMap<>();
		for (FieldError e : ex.getBindingResult().getFieldErrors())
			errors.putIfAbsent(e.getField(), e.getDefaultMessage());
		return response(HttpStatus.BAD_REQUEST, "Request validation failed", errors, req);
	}

	@ExceptionHandler(BadCredentialsException.class)
	ResponseEntity<ApiError> credentials(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.UNAUTHORIZED, "Invalid email or password", Map.of(), req);
	}

	@ExceptionHandler(AccessDeniedException.class)
	ResponseEntity<ApiError> denied(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.FORBIDDEN, "You are not allowed to perform this operation", Map.of(), req);
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	ResponseEntity<ApiError> optimisticLock(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.CONFLICT, "The resource was modified by another request. Please retry", Map.of(),
				req);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	ResponseEntity<ApiError> conflict(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.CONFLICT, "The request conflicts with existing data", Map.of(), req);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	ResponseEntity<ApiError> unreadable(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.BAD_REQUEST, "Malformed request body", Map.of(), req);
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<ApiError> unexpected(Exception ex, jakarta.servlet.http.HttpServletRequest req) {
		return response(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", Map.of(), req);
	}

	private ResponseEntity<ApiError> response(HttpStatus status, String message, Map<String, String> errors,
			jakarta.servlet.http.HttpServletRequest req) {
		return ResponseEntity.status(status).body(new ApiError(Instant.now(), status.value(), status.getReasonPhrase(),
				message, errors, req.getRequestURI()));
	}
}
