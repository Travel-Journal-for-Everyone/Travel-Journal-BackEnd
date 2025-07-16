package com.traveljournal.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ImageDeleteException extends RuntimeException {
	public ImageDeleteException(String message) {
		super(message);
	}

	public ImageDeleteException(String message, Throwable cause) {
		super(message, cause);
	}
}
