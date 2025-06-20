package com.traveljournal.domain.place.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.block.repository.BlockRepository;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.domain.place.entity.Place;
import com.traveljournal.domain.place.repository.PlaceRepository;
import com.traveljournal.global.util.RegionGroupUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final BlockRepository blockRepository;
	private final BlockService blockService;

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> findPlacesByRegionWithPaging(Long memberId, Long viewerId, String regionName, Pageable pageable) {

		blockService.validateNotBlocked(viewerId, memberId);

		List<String> regionList = RegionGroupUtil.getRegionList(regionName);

		List<Long> blockedIds = blockService.getBlockedMemberIds(viewerId);

		Page<Long> placeIdPage = placeRepository.findIdsByMemberIdAndRegionInExcludingBlocked(memberId, regionList, blockedIds, pageable);
		return getPlaceListResponses(pageable, placeIdPage);
	}

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> findAllPlacesByMemberId(Long memberId, Long viewerId, Pageable pageable) {

		blockService.validateNotBlocked(viewerId, memberId);

		List<Long> blockedIds = blockService.getBlockedMemberIds(viewerId);
		Page<Long> placeIdPage = placeRepository.findIdsByMemberIdExcludingBlocked(memberId, blockedIds, pageable);
		return getPlaceListResponses(pageable, placeIdPage);
	}

	private Page<PlaceListResponse> getPlaceListResponses(Pageable pageable, Page<Long> placeIdPage) {
		List<Long> placeIds = placeIdPage.getContent();
		List<Place> places = placeRepository.findAllByIdIn(placeIds);

		Map<Long, Place> placeMap = places.stream().collect(Collectors.toMap(Place::getId, p -> p));
		List<Place> sortedPlaces = placeIds.stream().map(placeMap::get).toList();

		return new PageImpl<>(
			sortedPlaces.stream()
				.map(PlaceListResponse::of)
				.toList(),
			pageable,
			placeIdPage.getTotalElements()
		);
	}
}