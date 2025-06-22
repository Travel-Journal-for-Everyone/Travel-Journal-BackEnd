package com.traveljournal.domain.statistics.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.statistics.entity.MemberRegionStatistics;
import com.traveljournal.domain.statistics.entity.MemberRegionStatisticsId;

public interface MemberRegionStatisticsRepository extends JpaRepository<MemberRegionStatistics, MemberRegionStatisticsId> {
	List<MemberRegionStatistics> findAllByIdMemberId(Long memberId);
}
