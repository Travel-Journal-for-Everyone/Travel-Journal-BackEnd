package com.traveljournal.domain.auth.util;

import java.net.URL;
import java.text.ParseException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.traveljournal.domain.auth.dto.google.GoogleIdTokenInfo;
import com.traveljournal.domain.auth.dto.google.GoogleMemberInfo;
import com.traveljournal.domain.auth.dto.google.GoogleTokenResponse;
import com.traveljournal.global.config.GoogleOAuthConfig;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.ExternalApiException;
import com.traveljournal.global.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient {
    private final GoogleOAuthConfig googleOAuthConfig;
    private final RestTemplate restTemplate;
    private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs";

    // 구글 인증 코드로 토큰 정보 요청
    public GoogleTokenResponse getGoogleToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", googleOAuthConfig.getClientId());
            params.add("client_secret", googleOAuthConfig.getClientSecret());
            params.add("redirect_uri", googleOAuthConfig.getRedirectUri());
            params.add("grant_type", "authorization_code");
            params.add("access_type", "offline");
            params.add("prompt", "consent");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            GoogleTokenResponse response = restTemplate.postForObject(
                googleOAuthConfig.getTokenUri(),
                request,
                GoogleTokenResponse.class
            );

            if (response == null) {
                throw new ExternalApiException("구글 토큰 응답이 null 입니다.");
            }
            return response;
        } catch (org.springframework.web.client.HttpClientErrorException.BadRequest e) {
            log.error("구글 토큰 발급 실패(400): {}", e.getMessage());
            throw new BadRequestException("구글 인증 코드가 잘못되었습니다: " + e.getMessage());
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            log.error("구글 토큰 발급 실패(401): {}", e.getMessage());
            throw new UnauthorizedException("구글 인증이 실패했습니다: " + e.getMessage());
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("구글 토큰 발급 실패(4xx/5xx): {}", e.getMessage());
            throw new ExternalApiException("구글 토큰 발급에 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            log.error("구글 토큰 발급 실패(기타): {}", e.getMessage());
            throw new ExternalApiException("구글 토큰 발급에 실패했습니다: " + e.getMessage());
        }
    }

    public GoogleMemberInfo getGoogleMemberInfo(String accessToken) {
        String memberInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;
        try {
            return restTemplate.getForObject(memberInfoUrl, GoogleMemberInfo.class);
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            throw new UnauthorizedException("구글 인증이 실패했습니다: " + e.getMessage());
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            throw new ExternalApiException("구글 사용자 정보 조회 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiException("구글 사용자 정보 조회 중 오류: " + e.getMessage());
        }
    }

    public static void verifyIdToken(String idToken) {
        try {
            URL jwkUrl = new URL(GOOGLE_JWK_URL);
            JWKSet jwkSet = JWKSet.load(jwkUrl);

            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String kid = signedJWT.getHeader().getKeyID();
            JWK jwk = jwkSet.getKeyByKeyId(kid);

            if (jwk == null) {
                throw new ExternalApiException("유효한 공개 키를 찾을 수 없습니다.");
            }

            RSASSAVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
            if (!signedJWT.verify(verifier)) {
                throw new UnauthorizedException("ID 토큰 서명 검증에 실패했습니다.");
            }
        } catch (ParseException | java.io.IOException e) {
            throw new BadRequestException("ID 토큰 파싱 또는 공개 키 로딩 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiException("ID 토큰 검증에 실패했습니다: " + e.getMessage());
        }
    }

    public GoogleIdTokenInfo getGoogleMemberInfoFromIdToken(String idToken) {
        try {
            verifyIdToken(idToken);

            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String sub = claims.getSubject();
            String email = claims.getStringClaim("email");
            String nickname = claims.getStringClaim("name");
            String picture = claims.getStringClaim("picture");

            return new GoogleIdTokenInfo(sub, email, nickname, picture);
        } catch (BadRequestException | UnauthorizedException | ExternalApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalApiException("ID Token 파싱에 실패했습니다: " + e.getMessage());
        }
    }

    public void revokeToken(String refreshToken) {
        String revokeUrl = "https://oauth2.googleapis.com/revoke?token=" + refreshToken;
        try {
            restTemplate.postForObject(revokeUrl, null, String.class);
        } catch (org.springframework.web.client.HttpClientErrorException.BadRequest e) {
            throw new BadRequestException("구글 토큰 해제 요청이 잘못되었습니다: " + e.getMessage());
        } catch (org.springframework.web.client.HttpClientErrorException.Unauthorized e) {
            throw new UnauthorizedException("구글 인증이 실패했습니다: " + e.getMessage());
        } catch (Exception e) {
            throw new ExternalApiException("구글 계정 연결 해제 실패: " + e.getMessage());
        }
    }
}