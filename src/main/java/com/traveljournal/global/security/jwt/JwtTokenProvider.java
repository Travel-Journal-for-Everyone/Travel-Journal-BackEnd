package com.traveljournal.global.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.traveljournal.global.config.AppConfig;
import com.traveljournal.global.data.JwtValidateStatus;
import com.traveljournal.global.security.service.CustomUserDetailsService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰을 관리하는 클래스
 * - 토큰 생성, 검증, 파싱 기능을 수행한다.
 * - Access Token과 Refresh Token을 구분하여 처리한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

	private final AppConfig appConfig;
	private final CustomUserDetailsService userDetailsService;

	private SecretKey secretKey;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration; // Access Token 유효 기간

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration; // Refresh Token 유효 기간

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(appConfig.getSecretKey());
	}

	/**
	 * JWT Access Token 생성
	 */
	public String createAccessToken(String email) {
		return createToken(email, accessTokenExpiration);
	}

	/**
	 * JWT Refresh Token 생성
	 */
	public String createRefreshToken(String email) {
		return createToken(email, refreshTokenExpiration);
	}

	/**
	 * JWT 토큰 생성 공통 로직
	 */
	private String createToken(String email, long expirationTime) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + expirationTime);

		return Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(secretKey)
			.compact();
	}

	/**
	 * JWT 토큰에서 이메일 추출
	 */
	public String getEmail(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
		} catch (ExpiredJwtException e) {
			// 만료된 토큰이라도 이메일은 추출
			return e.getClaims().getSubject();
		}
	}

	/**
	 * 토큰 검증을 위한 메서드
	 * @param token 접근 토큰 혹은 갱신 토큰
	 * @return JwtValidateStatus
	 *  ACCEPTED 검증 완료
	 *  EXPIRED 만료
	 *  DENIED 검증 실패
	 */
	public JwtValidateStatus getTokenValidationStatus(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);
			return JwtValidateStatus.ACCEPTED;
		} catch (ExpiredJwtException e) {
			return JwtValidateStatus.EXPIRED;
		} catch (JwtException e) {
			return JwtValidateStatus.DENIED;
		}
	}

	/**
	 * JWT 토큰에서 Claims 추출
	 */
	public Claims getClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			// 만료된 토큰이라도 Claims 반환
			return e.getClaims();
		}
	}

	/**
	 * 요청 헤더에서 JWT 토큰 추출
	 */
	public String resolveToken(String bearerToken) {
		validateAuthorizationHeader(bearerToken);
		if (bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private void validateAuthorizationHeader(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("유효한 Authorization 헤더가 필요합니다.");
		}
	}

	/**
	 * 인증 객체를 SecurityContext에 저장
	 */
	public void setAuthentication(String token) {
		String email = getEmail(token);
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}