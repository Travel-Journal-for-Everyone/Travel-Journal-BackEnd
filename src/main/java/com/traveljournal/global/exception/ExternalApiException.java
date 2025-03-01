package com.traveljournal.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ExternalApiException extends RuntimeException {

	public ExternalApiException(String message) {
		super(message);
	}

	public ExternalApiException() {
		super("외부 API 호출에 실패했습니다.");
	}
}