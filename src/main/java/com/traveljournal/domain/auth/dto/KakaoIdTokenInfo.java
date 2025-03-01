package com.traveljournal.domain.auth.dto;

import lombok.Builder;

@Builder
public record KakaoIdTokenInfo(
	String sub,        // 카카오 회원번호 (고유 ID)
	String email,      // 이메일
	String nickname,   // 닉네임
	String profile_image_url     // 프로필 이미지 URL
) {}