package com.traveljournal.global.security.jwt;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.traveljournal.global.data.JwtValidateStatus;

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
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		// 헤더에서 Authorization 값 추출
		String authorization = request.getHeader("Authorization");

		// 토큰이 있는 경우에만 처리
		if (authorization != null) {
			// Bearer 토큰 추출
			String token = jwtTokenProvider.resolveToken(authorization);

			if (token != null) {
				// 토큰 유효성 검사
				JwtValidateStatus status = jwtTokenProvider.getTokenValidationStatus(token);

				if (status == JwtValidateStatus.ACCEPTED) {
					try {
						// 인증 정보 설정
						jwtTokenProvider.setAuthentication(token);
						log.debug("토큰 인증 성공");
					} catch (Exception e) {
						log.error("토큰 처리 중 에러 발생: {}", e.getMessage());
						SecurityContextHolder.clearContext();
					}
				} else {
					log.debug("유효하지 않은 토큰: {}", status);
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}