package com.traveljournal.domain.auth.dto.google;

// 구글 로그인 후 반환되는 토큰 정보를 받아올 DTO
public record GoogleTokenResponse (
    String access_token,
    String token_type,
    String id_token,
    String scope
){ }
