package com.traveljournal.domain.auth.service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.dto.apple.AppleIdTokenInfo;
import com.traveljournal.domain.auth.dto.apple.AppleKeyInfo;
import com.traveljournal.domain.auth.dto.apple.AppleMemberInfo;
import com.traveljournal.domain.auth.dto.apple.ApplePublicKeyResponse;
import com.traveljournal.domain.auth.dto.apple.AppleTokenResponse;
import com.traveljournal.domain.auth.util.AppleClient;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.member.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppleService {
	private final AppleClient appleClient;
	private final TokenService tokenService;
	private final MemberService memberService;
	private final ObjectMapper objectMapper;

	@Value("${oauth.apple.team-id}")
	private String teamId;

	@Value("${oauth.apple.services-id}")
	private String servicesId;

	@Value("${oauth.apple.key-id}")
	private String keyId;

	@Value("${oauth.apple.private-key-path}")
	private String privateKeyPath;

	@Value("${oauth.apple.redirect-uri}")
	private String redirectUri;

	@Value("${oauth.apple.ios-bundle-id}")
	private String iosBundleId;

	@Value("${oauth.apple.android-bundle-id}")
	private String androidBundleId;

	/**
	 * 애플 로그인 처리
	 * 1. 애플 인증 코드로 토큰 요청
	 * 2. ID 토큰 검증 및 사용자 정보 추출
	 * 3. 이메일로 회원 조회 또는 생성
	 * 4. JWT 토큰 생성 및 저장
	 * 5. 로그인 응답 생성
	 */
	public LoginCombinedResponse processAppleLoginWithCode(String code, String deviceId, SocialProvider socialProvider,
		String platform) {
		// 애플 토큰 획득
		AppleTokenResponse appleTokenResponse = getAppleToken(code);

		return processAppleLoginWithIdToken(appleTokenResponse.idToken(), deviceId, socialProvider, platform);
	}

	public LoginCombinedResponse processAppleLoginWithIdToken(String idToken, String deviceId,
		SocialProvider socialProvider, String platform) {
		// ID 토큰 검증 및 사용자 정보 추출
		AppleIdTokenInfo appleIdTokenInfo = verifyAndParseIdToken(idToken, platform);

		// 회원 찾기 또는 생성
		Member member = getMemberFromIdToken(appleIdTokenInfo, socialProvider);

		// JWT 토큰 생성 및 저장
		TokenInfo tokenInfo = createAndSaveTokens(member, deviceId);

		return createLoginResponse(member, tokenInfo);
	}

	// 애플 토큰 요청
	public AppleTokenResponse getAppleToken(String code) {
		String clientSecret = createClientSecret();
		return appleClient.appleAuth(
			servicesId,
			code,
			"authorization_code",
			clientSecret,
			redirectUri
		);
	}

	// JWT 클라이언트 시크릿 생성
	private String createClientSecret() {
		Date expirationDate = Date.from(
			LocalDateTime.now().plusMinutes(5)
				.atZone(ZoneId.systemDefault())
				.toInstant()
		);

		return Jwts.builder()
			.setHeaderParam("kid", keyId)
			.setHeaderParam("alg", "ES256")
			.setIssuer(teamId)
			.setIssuedAt(new Date())
			.setExpiration(expirationDate)
			.setAudience("https://appleid.apple.com")
			.setSubject(servicesId)
			.signWith(getPrivateKey(), SignatureAlgorithm.ES256)
			.compact();
	}

	// 프라이빗 키 로드
	private PrivateKey getPrivateKey() {
		try {
			Resource resource = new ClassPathResource(privateKeyPath.replace("classpath:", ""));
			String privateKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
			privateKey = privateKey
				.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");

			byte[] keyBytes = Base64.getDecoder().decode(privateKey);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			return keyFactory.generatePrivate(keySpec);
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new RuntimeException("Failed to load private key", e);
		}
	}

	// ID 토큰 검증 및 파싱
	public AppleIdTokenInfo verifyAndParseIdToken(String idToken, String platform) {
		try {
			// 토큰 파싱
			String[] tokenParts = idToken.split("\\.");
			if (tokenParts.length != 3) {
				throw new RuntimeException("Invalid ID token format");
			}

			// 헤더 파싱
			String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]));
			Map<String, String> header = objectMapper.readValue(headerJson, Map.class);
			String kid = header.get("kid");
			String alg = header.get("alg");

			// 애플 공개키 가져오기
			ApplePublicKeyResponse keyResponse = appleClient.getApplePublicKeys();

			// kid와 alg에 맞는 공개키 찾기
			AppleKeyInfo matchingKey = keyResponse.keys().stream()
				.filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("No matching key found"));

			// 공개키 생성
			PublicKey publicKey = generatePublicKey(matchingKey);

			// 토큰 검증
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(publicKey)
				.build()
				.parseClaimsJws(idToken)
				.getBody();

			// 클레임 검증
			String issuer = claims.getIssuer();
			if (!"https://appleid.apple.com".equals(issuer)) {
				throw new RuntimeException("Invalid issuer: " + issuer);
			}

			String audience = claims.getAudience();
			String expectedAudience;

			if ("ios".equals(platform)) {
				expectedAudience = iosBundleId;
			} else if ("android".equals(platform)) {
				expectedAudience = androidBundleId;
			} else {
				expectedAudience = servicesId; // 웹
			}

			if (!expectedAudience.equals(audience)) {
				throw new RuntimeException("Invalid audience: " + audience);
			}

			Date expirationTime = claims.getExpiration();
			if (expirationTime.before(new Date())) {
				throw new RuntimeException("Token expired");
			}

			// 사용자 정보 추출
			String subject = claims.getSubject(); // 애플 사용자 ID
			String email = claims.get("email", String.class);

			// email_verified 필드 null 체크 추가
			Boolean emailVerifiedClaim = claims.get("email_verified", Boolean.class);
			boolean emailVerified = emailVerifiedClaim != null && emailVerifiedClaim;

			// 사용자 정보가 담긴 객체 반환
			return new AppleIdTokenInfo(subject, email, emailVerified);
		} catch (Exception e) {
			throw new RuntimeException("ID 토큰 검증 실패: " + e.getMessage(), e);
		}
	}

	// 공개키 생성
	private PublicKey generatePublicKey(AppleKeyInfo keyInfo) throws NoSuchAlgorithmException, InvalidKeySpecException {
		BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(keyInfo.n()));
		BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(keyInfo.e()));

		RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(publicKeySpec);
	}

	private Member getMemberFromIdToken(AppleIdTokenInfo appleIdTokenInfo, SocialProvider socialProvider) {
		return memberService.findOrCreateMember(
			new AppleMemberInfo(
				appleIdTokenInfo.sub(),
				appleIdTokenInfo.email(),
				null // 애플은 프로필 이미지를 제공하지 않음
			),
			socialProvider
		);
	}

	private TokenInfo createAndSaveTokens(Member member, String deviceId) {
		// JWT 토큰 생성
		TokenInfo tokenInfo = tokenService.createTokens(member.getProviderId(), deviceId);

		// 토큰 저장
		tokenService.saveOrUpdateToken(member, tokenInfo.deviceId(), tokenInfo.refreshToken());

		return tokenInfo;
	}

	private LoginCombinedResponse createLoginResponse(Member member, TokenInfo tokenInfo) {
		return LoginCombinedResponse.of(LoginResponse.from(member, tokenInfo), tokenInfo.accessToken());
	}
}
