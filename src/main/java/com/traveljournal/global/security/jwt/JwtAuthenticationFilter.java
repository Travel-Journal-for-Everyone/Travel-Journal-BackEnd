package com.traveljournal.global.security.jwt;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 인증 필터
 * 요청이 올 때마다 실행되며, JWT 토큰을 검증하고 인증 정보를 설정한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String token = jwtTokenProvider.resolveToken(request.getHeader("Authorization"));

		if (token != null && jwtTokenProvider.validateToken(token)) {
			jwtTokenProvider.setAuthentication(token);
		} else {
			log.warn("Invalid or missing JWT token");
		}

		filterChain.doFilter(request, response);
	}
}