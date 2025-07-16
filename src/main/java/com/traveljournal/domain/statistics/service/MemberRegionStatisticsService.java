package com.traveljournal.domain.statistics.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.statistics.entity.MemberRegionStatistics;
import com.traveljournal.domain.statistics.entity.MemberRegionStatisticsId;
import com.traveljournal.domain.statistics.repository.MemberRegionStatisticsRepository;
import com.traveljournal.global.util.RegionNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberRegionStatisticsService {

	private final MemberRegionStatisticsRepository memberRegionStatisticsRepository;

	@Transactional
	public void increaseTravelDiaryCount(Long memberId, String rawRegion) {
		String regionGroup = RegionNormalizer.normalize(rawRegion);
		MemberRegionStatisticsId regionStatsId = new MemberRegionStatisticsId(memberId, regionGroup);

		MemberRegionStatistics regionStats = memberRegionStatisticsRepository.findById(regionStatsId)
			.orElseGet(() -> memberRegionStatisticsRepository.save(
				new MemberRegionStatistics(regionStatsId, 0L, 0L)
			));
		regionStats.increaseTravelDiaryCount();
	}

	@Transactional
	public void decreaseTravelDiaryCount(Long memberId, String rawRegion) {
		String regionGroup = RegionNormalizer.normalize(rawRegion);
		MemberRegionStatisticsId regionStatsId = new MemberRegionStatisticsId(memberId, regionGroup);

		MemberRegionStatistics regionStats = memberRegionStatisticsRepository.findById(regionStatsId)
			.orElse(null);

		if (regionStats != null) {
			regionStats.decreaseTravelDiaryCount();
			if (regionStats.getTravelDiaryCount() <= 0) {
				memberRegionStatisticsRepository.delete(regionStats);
			}
		}
	}
}
