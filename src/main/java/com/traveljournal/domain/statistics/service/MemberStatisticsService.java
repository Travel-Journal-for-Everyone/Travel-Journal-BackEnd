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

	@Transactional
	public void increaseTravelDiaryCount(Long memberId) {
		MemberStatistics stats = memberStatisticsRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("통계 정보 없음"));
		stats.increaseTravelDiaryCount();
	}
}
