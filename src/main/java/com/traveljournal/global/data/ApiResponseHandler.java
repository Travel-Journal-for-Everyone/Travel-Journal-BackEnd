package com.traveljournal.global.data;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponseHandler<T>{

	private ApiResponseHandler() {
	}

	public static <LoginResponse> ResponseEntity<LoginResponse> accessTokenResponse(LoginResponse loginResponse,
		String accessToken) {
		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + accessToken)
			.body(loginResponse);
	}

	public static ResponseEntity<?> createdSuccess(String message) {
		return ResponseEntity.status(HttpStatus.CREATED).body(message);
	}

	public static ResponseEntity<?> onSuccess(String message) {
		return ResponseEntity.ok(message);
	}

	public static ResponseEntity<?> deletedSuccess(String message) {
		return ResponseEntity.ok(message);
	}

	public static ResponseEntity<?> updatedSuccess(String message) {
		return ResponseEntity.ok(message);
	}

	public static <T> ResponseEntity<T> getObjectSuccess(T object) {
		return ResponseEntity.ok(object);
	}

	public static ResponseEntity<?> onFailure(String message) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
	}

}