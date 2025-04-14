package com.traveljournal.domain.search.dto;

import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberSearchResponse (
        Long memberId,
        String nickname,
        String profileImageUrl,
        Long travelDiaryCount,
        Long placesCount
) {
    public static MemberSearchResponse of(Long id, MemberProfileResponse profileResponse) {
        return MemberSearchResponse.builder()
                .memberId(id)
                .nickname(profileResponse.nickname())
                .profileImageUrl(profileResponse.profileImageUrl())
                .travelDiaryCount(profileResponse.travelDiaryCount())
                .placesCount(profileResponse.placesCount())
                .build();
    }
}
