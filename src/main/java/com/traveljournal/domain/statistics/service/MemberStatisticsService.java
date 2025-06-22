package com.traveljournal.domain.statistics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.statistics.entity.MemberStatistics;
import com.traveljournal.domain.statistics.repository.MemberStatisticsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberStatisticsService {
	private final MemberStatisticsRepository memberStatisticsRepository;

	private MemberStatistics findByMemberId(Long memberId) {
		return memberStatisticsRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("통계 정보 없음"));
	}

	@Transactional
	public void increaseTravelDiaryCount(Long memberId) {
		MemberStatistics stats = findByMemberId(memberId);
		stats.increaseTravelDiaryCount();
	}

	@Transactional
	public void increaseFollowerCount(Long memberId) {
		MemberStatistics stats = findByMemberId(memberId);
		stats.increaseFollowerCount();
	}

	@Transactional
	public void increaseFollowingCount(Long memberId) {
		MemberStatistics stats = findByMemberId(memberId);
		stats.increaseFollowingCount();
	}

	@Transactional
	public void decreaseFollowerCount(Long memberId) {
		MemberStatistics stats = findByMemberId(memberId);
		stats.decreaseFollowerCount();
	}

	@Transactional
	public void decreaseFollowingCount(Long memberId) {
		MemberStatistics stats = findByMemberId(memberId);
		stats.decreaseFollowingCount();
	}
}
