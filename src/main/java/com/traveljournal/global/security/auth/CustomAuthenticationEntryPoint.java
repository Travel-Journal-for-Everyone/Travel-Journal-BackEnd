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

		// 응답이 이미 커밋되었으면 예외 처리 중단
		if (response.isCommitted()) {
			return;
		}

		// 상태 코드 설정
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		// 캐싱 방지 헤더 추가
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

		// 메시지 반환
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// JSON 응답 생성
		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", "Unauthorized: 인증이 필요합니다.");
		errorResponse.put("status", "401");

		String jsonResponse = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(jsonResponse);
	}
}
