package com.traveljournal.domain.auth.service;

import com.traveljournal.domain.auth.dto.*;
import com.traveljournal.domain.auth.dto.google.GoogleIdTokenInfo;
import com.traveljournal.domain.auth.dto.google.GoogleMemberInfo;
import com.traveljournal.domain.auth.dto.google.GoogleTokenResponse;
import com.traveljournal.domain.auth.util.GoogleClient;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.member.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final TokenService tokenService;
    private final MemberService memberService;
    private final GoogleClient googleClient;

    public LoginCombinedResponse processGoogleLoginWithCode(String code, String deviceId, SocialProvider socialProvider) {
        // 구글 토큰 획득
        GoogleTokenResponse googleTokenResponse = googleClient.getGoogleToken(code);

        return processGoogleLoginWithIdToken(googleTokenResponse.id_token(), deviceId, socialProvider);
    }

    public LoginCombinedResponse processGoogleLoginWithIdToken(String idToken, String deviceId, SocialProvider socialProvider) {
        // ID Token 으로 구글 사용자 정보 가져오기
        GoogleIdTokenInfo googleIdTokenInfo = googleClient.getGoogleMemberInfoFromIdToken(idToken);

        // 회원 찾기 또는 생성
        Member member = getMemberFromGoogleIdToken(googleIdTokenInfo, socialProvider);

        // JWT 토큰 생성 및 저장
        TokenInfo tokenInfo = createAndSaveTokens(member, deviceId);

        // 로그인 응답 생성
        return createLoginResponse(member, tokenInfo);
    }

    private Member getMemberFromGoogleIdToken(GoogleIdTokenInfo googleIdTokenInfo, SocialProvider socialProvider) {
        return memberService.findOrCreateMemberGoogle(
                GoogleMemberInfo.builder()
                        .email(googleIdTokenInfo.email())
                        .name(googleIdTokenInfo.nickname())
                        .picture(googleIdTokenInfo.profile_image_url())
                        .build(), socialProvider
        );
    }

    private TokenInfo createAndSaveTokens(Member member, String deviceId) {
        // JWT 토큰 생성
        TokenInfo tokenInfo = tokenService.createTokens(member.getEmail(), deviceId);

        // 토큰 저장
        tokenService.saveOrUpdateToken(member, tokenInfo.deviceId(), tokenInfo.refreshToken());

        return tokenInfo;
    }

    private LoginCombinedResponse createLoginResponse(Member member, TokenInfo tokenInfo) {
        return LoginCombinedResponse.of(LoginResponse.from(member, tokenInfo), tokenInfo.accessToken());
    }
}