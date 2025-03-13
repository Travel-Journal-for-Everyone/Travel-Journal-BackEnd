package com.traveljournal.global.data;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseHandler {

	/**
	 * 일반 성공 응답
	 */
	public static <T> ResponseEntity<ResponseDto<T>> ok(T data) {
		ResponseDto<T> result = ResponseDto.<T>builder()
			.success(true)
			.message("요청이 성공적으로 처리되었습니다.")
			.data(data)
			.build();
		return ResponseEntity.ok(result);
	}

	/**
	 * 생성 성공 응답
	 */
	public static <T> ResponseEntity<ResponseDto<T>> created(T data) {
		ResponseDto<T> result = ResponseDto.<T>builder()
			.success(true)
			.message("데이터가 성공적으로 생성되었습니다.")
			.data(data)
			.build();
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	/**
	 * 성공 응답 (수정, 삭제)
	 */
	public static ResponseEntity<ResponseDto<Void>> success(String message) {
		ResponseDto<Void> result = ResponseDto.<Void>builder()
			.success(true)
			.message(message)
			.build();
		return ResponseEntity.ok(result);
	}

	/**
	 * 액세스 토큰을 헤더에 포함하는 응답
	 */
	public static <T> ResponseEntity<T> accessTokenResponse(T data, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);

		return ResponseEntity.ok()
			.headers(headers)
			.body(data);
	}

	/**
	 * 실패 응답
	 */
	public static ResponseEntity<ResponseDto<Void>> onFailure(String message) {
		ResponseDto<Void> result = ResponseDto.<Void>builder()
			.success(false)
			.message(message)
			.build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
	}
}