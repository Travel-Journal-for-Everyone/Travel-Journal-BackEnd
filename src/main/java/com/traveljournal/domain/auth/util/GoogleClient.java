package com.traveljournal.domain.auth.util;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.traveljournal.domain.auth.dto.google.GoogleIdTokenInfo;
import com.traveljournal.domain.auth.dto.google.GoogleMemberInfo;
import com.traveljournal.domain.auth.dto.google.GoogleTokenResponse;
import com.traveljournal.global.config.GoogleOAuthConfig;
import com.traveljournal.global.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleClient {
    private final GoogleOAuthConfig googleOAuthConfig;
    private final RestTemplate restTemplate;
    private static final String GOOGLE_JWK_URL = "https://www.googleapis.com/oauth2/v3/certs";
    /**
     * 구글 인증 코드로 토큰 정보 요청
     */
    public GoogleTokenResponse getGoogleToken(String code) {
        /*
        String url = "https://accounts.google.com/o/oauth2/v2/auth?";
        String body = "code=" + code +
                "&client_id=" + "${google.clientId}" +
                "&client_secret=" + "${google.clientSecret}" +
                "&redirect_uri=" + "${google.RedirectUri}" +
                "&grant_type=authorization_code";

        return restTemplate.postForObject(url, body, GoogleTokenResponse.class);

         */
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", googleOAuthConfig.getClientId());
            params.add("client_secret", googleOAuthConfig.getClientSecret());
            params.add("redirect_uri", googleOAuthConfig.getRedirectUri());
            params.add("grant_type", "authorization_code");
            log.info("🚀 Google OAuth 요청 - clientId: {}, clientSecret: {}, redirectUri: {}",
                    googleOAuthConfig.getClientId(),
                    googleOAuthConfig.getClientSecret(),
                    googleOAuthConfig.getRedirectUri());
            log.info("🚀 [Google OAuth] 토큰 요청 URL: {}", googleOAuthConfig.getTokenUri());
            log.info("🚀 [Google OAuth] 요청 파라미터: {}", params);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            GoogleTokenResponse response = restTemplate.postForObject(
                    googleOAuthConfig.getTokenUri(),
                    request,
                    GoogleTokenResponse.class
            );

            log.info("✅ [Google OAuth] 토큰 발급 성공: {}", response);

            if (response == null) {
                throw new ExternalApiException("구글 토큰 응답이 null 입니다.");
            }
            log.info("구글 OAuth 응답 - ID 토큰: {}", response);
            log.info("구글 토큰 발급 성공");
            return response;
        } catch (Exception e) {
            log.error("구글 토큰 발급 실패 : {}", e.getMessage());
            throw new ExternalApiException("구글 토큰 발급에 실패했습니다." + e.getMessage());
        }
    }

    /**
     * 구글 액세스 토큰으로 사용자 정보 가져오기
     */

    public GoogleMemberInfo getGoogleMemberInfo(String accessToken) {
        String memberInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo?access_token=" + accessToken;

        log.info("구글 사용자 정보 요청: {}", memberInfoUrl);

        return restTemplate.getForObject(memberInfoUrl, GoogleMemberInfo.class);

    }

    /**
     * ID Token 서명 검증
     */
    public static void verifyIdToken(String idToken) throws Exception{
        try {

            // 공개 키 가져오기
            URL jwkUrl = new URL(GOOGLE_JWK_URL);
            JWKSet jwkSet = JWKSet.load(jwkUrl);

            // ID Token 파싱
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // JWT 헤더에서 키 ID 추출
            // 공개 키 찾기 위한 kid 값 추출
            String kid = signedJWT.getHeader().getKeyID();

            // 공개 키 찾기
            JWK jwk = jwkSet.getKeyByKeyId(kid);  // 공개 키 찾기

            // 공개 키 셋과 kid 값 확인
            log.info("JWK Set: {}", jwkSet);
            log.info("kid: {}", kid);

            // 공개 키 확인
            log.info("JWK: {}", jwk);
            if (jwk == null) {
                throw new ExternalApiException("유효한 공개 키를 찾을 수 없습니다.");
            }

            // 공개 키로 RSASSAVerifier 생성
            RSASSAVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());

            // 서명 검증
            if (!signedJWT.verify(verifier)) {
                throw new ExternalApiException("ID 토큰 서명 검증에 실패했습니다.");
            }

            log.info("구글 ID 토큰 서명 검증 성공");

        } catch (ParseException | java.io.IOException e) {
            log.error("ID 토큰 파싱 또는 공개 키 로딩 실패 : {}", e.getMessage());
            throw new ExternalApiException("ID 토큰 검증에 실패했습니다." + e.getMessage());
        }
    }
    public GoogleIdTokenInfo getGoogleMemberInfoFromIdToken(String idToken) {
        try {

            log.info("받은 ID 토큰: {}", idToken);
            // ID Token 서명 검증
            verifyIdToken(idToken);

            // ID Token 디코딩
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            log.info("Signed JWT decoded successfully: {}", signedJWT);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String sub = claims.getSubject();
            String email = claims.getStringClaim("email");
            String nickname = claims.getStringClaim("name");
            String picture = claims.getStringClaim("picture");

            log.info("구글 ID Token 정보 : sub={}, email={}, nickname={}, picture={}", sub, email, nickname, picture);
            return new GoogleIdTokenInfo(sub, email, nickname, picture);

        } catch (Exception e) {
            log.error("ID Token 파싱 실패 : {}", e.getMessage());
            throw new ExternalApiException("ID Token 파싱에 실패했습니다." + e.getMessage());
        }
    }
}

