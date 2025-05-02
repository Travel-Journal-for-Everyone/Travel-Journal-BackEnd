package com.traveljournal.global.security.jwt;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.traveljournal.global.data.JwtValidateStatus;
import com.traveljournal.global.exception.UserNotFoundException;

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
			String token = jwtTokenProvider.resolveToken(authorization);

			if (token != null) {
				try {
					JwtValidateStatus status = jwtTokenProvider.getTokenValidationStatus(token);

					if (status == JwtValidateStatus.ACCEPTED) {
						jwtTokenProvider.setAuthentication(token);
						log.error("토큰 인증 성공");
					} else if (status == JwtValidateStatus.EXPIRED) {
						request.setAttribute("exception", "TOKEN_EXPIRED");
						log.error("토큰 만료");
						SecurityContextHolder.clearContext();
					} else if (status == JwtValidateStatus.INVALID) {
						request.setAttribute("exception", "TOKEN_INVALID");
						log.error("유효하지 않은 토큰");
						SecurityContextHolder.clearContext();
					} else {
						request.setAttribute("exception", "TOKEN_UNKNOWN");
						log.error("알 수 없는 토큰 오류");
						SecurityContextHolder.clearContext();
					}
				} catch (UserNotFoundException e) {
					request.setAttribute("exception", "USER_NOT_FOUND");
					log.debug("사용자를 찾을 수 없습니다: {}", e.getMessage());
					SecurityContextHolder.clearContext();
				} catch (Exception e) {
					log.error("토큰 처리 중 에러 발생: {}", e.getMessage());
					request.setAttribute("exception", "TOKEN_ERROR");
					SecurityContextHolder.clearContext();
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}