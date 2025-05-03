package com.traveljournal.domain.place.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	Page<Place> findByMemberIdAndRegionContaining(Long memberId, String region, Pageable pageable);

	Page<Place> findByMemberId(Long memberId, Pageable pageable);
}
