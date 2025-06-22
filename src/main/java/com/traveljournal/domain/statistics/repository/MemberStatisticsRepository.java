package com.traveljournal.domain.statistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.statistics.entity.MemberStatistics;
public interface MemberStatisticsRepository extends JpaRepository<MemberStatistics, Long> {
}
