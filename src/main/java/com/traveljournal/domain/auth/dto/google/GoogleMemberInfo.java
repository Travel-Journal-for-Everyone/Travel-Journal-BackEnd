package com.traveljournal.domain.auth.dto.google;

import lombok.Builder;

// 구글 사용자 정보를 담을 DTO
@Builder
public record GoogleMemberInfo(
        // Google id 필드 : 문자열
        String id,
        String email,
        String name,
        String picture

) {


}
