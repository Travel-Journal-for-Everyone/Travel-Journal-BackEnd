package com.traveljournal.domain.place.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.domain.place.entity.Place;
import com.traveljournal.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> findPlacesByRegionWithPagion(Long memberId, String regionName, Pageable pageable) {
		Page<Place> places = placeRepository.findByMemberIdAndRegionContaining(memberId, regionName, pageable);
		return places.map(place -> new PlaceListResponse(
			place.getId(),
			place.getTitle(),
			place.getRegion(),
			place.getThumbnailUrl()
		));
	}

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> findAllPlacesByMemberId(Long memberId, Pageable pageable) {
		// 회원별 플레이스 조회 로직 필요시 구현
		Page<Place> places = placeRepository.findByMemberId(memberId, pageable);
		return places.map(place -> new PlaceListResponse(
			place.getId(),
			place.getTitle(),
			place.getRegion(),
			place.getThumbnailUrl()
		));
	}
}