package com.traveljournal.global.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.traveljournal.global.config.AppConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰을 관리하는 클래스
 * - 토큰 생성, 검증, 파싱 기능을 수행한다.
 */

/**
 * JWT 토큰을 관리하는 클래스
 * - 토큰 생성, 검증, 파싱 기능을 수행한다.
 * - Access Token과 Refresh Token을 구분하여 처리한다.
 */
@Slf4j
@Component
public class JwtTokenProvider {

	private final SecretKey secretKey;
	private final UserDetailsService userDetailsService;

	@Value("${jwt.access-token-expiration}")
	private long accessTokenExpiration; // Access Token 유효 기간

	@Value("${jwt.refresh-token-expiration}")
	private long refreshTokenExpiration; // Refresh Token 유효 기간

	public JwtTokenProvider(AppConfig appConfig, UserDetailsService userDetailsService) {
		// Base64로 디코딩한 secretKey를 생성
		this.secretKey = Keys.hmacShaKeyFor(appConfig.getSecretKey()); // 추가 디코딩 제거

		this.userDetailsService = userDetailsService;
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
	 * JWT 토큰에서 email 추출
	 */
	public String getEmail(String token) {
		return parseClaims(token).getBody().getSubject();
	}

	/**
	 * JWT 토큰 검증
	 */
	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			log.warn("Expired JWT Token: {}", e.getMessage());
		} catch (JwtException e) {
			log.warn("Invalid JWT Token: {}", e.getMessage());
		}
		return false;
	}

	/**
	 * JWT 토큰에서 Claims 추출
	 */
	private Jws<Claims> parseClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token);
	}

	/**
	 * 요청 헤더에서 JWT 토큰 추출
	 */
	public String resolveToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	/**
	 * 인증 객체를 SecurityContext에 저장
	 */
	public void setAuthentication(String token) {
		String email = getEmail(token);

		// userDetailsService를 통해 UserDetails 로드
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		Authentication authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

}