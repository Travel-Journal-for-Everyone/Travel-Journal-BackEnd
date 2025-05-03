package com.traveljournal.domain.search.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.domain.place.entity.Place;
import com.traveljournal.domain.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceSearchService {

	private final PlaceRepository placeRepository;

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> searchPlaces(String keyword, Pageable pageable) {
		Page<Long> placeIdPage = placeRepository.findIdsByTitleOrRegionContaining(keyword, pageable);
		List<Long> placeIds = placeIdPage.getContent();
		List<Place> places = placeRepository.findAllByIdIn(placeIds);

		Map<Long, Place> placeMap = places.stream()
			.collect(Collectors.toMap(Place::getId, p -> p));
		List<Place> sortedPlaces = placeIds.stream()
			.map(placeMap::get)
			.toList();

		return new PageImpl<>(
			sortedPlaces.stream()
				.map(place -> new PlaceListResponse(
					place.getId(),
					place.getTitle(),
					place.getRegion(),
					place.getThumbnailUrl()
				))
				.toList(),
			pageable,
			placeIdPage.getTotalElements()
		);
	}
}