package com.traveljournal.domain.search.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.block.repository.BlockRepository;
import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.statistics.entity.MemberStatistics;
import com.traveljournal.domain.statistics.repository.MemberStatisticsRepository;
import com.traveljournal.domain.search.dto.MemberSearchResponse;
import com.traveljournal.domain.search.repository.MemberSearchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberSearchService {

    private final MemberSearchRepository memberSearchRepository;
    private final MemberStatisticsRepository memberStatisticsRepository;
    private final BlockRepository blockRepository;

    // @Transactional(readOnly = true)
    // public Page<MemberSearchResponse> searchMembers(String keyword, Pageable pageable) {
    //
    //     Page<Member> membersPage = memberSearchRepository.findByNicknameContaining(keyword, pageable);
    //
    //     return membersPage.map(member ->
    //             MemberSearchResponse.of(
    //                     member.getId(),
    //                     MemberProfileResponse.of(member)
    //             )
    //     );
    // }

    @Transactional(readOnly = true)
    public Page<MemberSearchResponse> searchByNickname(String keyword, Pageable pageable, Long currentMemberId) {

        // currentMemberId를 넘겨서 repository 쿼리에서 차단 회원 자동 제외
        Page<Member> members = memberSearchRepository.findByNicknameContainingAndIdNotIn(keyword, currentMemberId, pageable);

        List<Long> memberIds = members.stream().map(Member::getId).toList();

        Map<Long, MemberStatistics> statsMap = memberStatisticsRepository.findAllById(memberIds).stream()
            .collect(Collectors.toMap(MemberStatistics::getMemberId, s -> s));

        return members.map(member -> {
            MemberStatistics stats = statsMap.get(member.getId());
            MemberProfileResponse profile = MemberProfileResponse.of(member, stats);
            return MemberSearchResponse.of(member.getId(), profile);
        });
    }

}
