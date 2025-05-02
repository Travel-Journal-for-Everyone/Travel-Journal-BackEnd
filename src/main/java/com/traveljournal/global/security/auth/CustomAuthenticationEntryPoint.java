package com.traveljournal.global.security.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException authException) throws IOException, ServletException {

		if (response.isCommitted()) {
			return;
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String exception = (String) request.getAttribute("exception");
		String message;
		String code;

		if ("USER_NOT_FOUND".equals(exception)) {
			code = "USER_NOT_FOUND";
			message = "사용자를 찾을 수 없습니다 -> 토큰에 담겨 있는 sub에 맞는 회원번호가 없습니다.";
		}
		else if ("TOKEN_EXPIRED".equals(exception)) {
			code = "TOKEN_EXPIRED";
			message = "토큰이 만료되었습니다.";
		} else if ("TOKEN_INVALID".equals(exception)) {
			code = "TOKEN_INVALID";
			message = "유효하지 않은 토큰입니다.";
		} else if ("TOKEN_UNKNOWN".equals(exception)) {
			code = "TOKEN_UNKNOWN";
			message = "알 수 없는 토큰 오류입니다.";
		} else if ("TOKEN_ERROR".equals(exception)) {
			code = "TOKEN_ERROR";
			message = "토큰 처리 중 오류가 발생했습니다.";
		} else {
			code = "UNAUTHORIZED";
			message = "Unauthorized: 인증이 필요합니다.";
		}

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("code", code);
		errorResponse.put("message", message);
		errorResponse.put("status", "401");

		String jsonResponse = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}
}
