package com.traveljournal.domain.place.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.global.dummy.DummyDataProvider;
import com.traveljournal.global.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final DummyDataProvider dummyDataProvider;

	private List<PlaceListResponse> findPlacesByRegion(String regionName) {
		return dummyDataProvider.getDummyPlacesByRegion(regionName);
	}

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> findPlacesByRegionWithPagion(String regionName, Pageable pageable) {
		List<PlaceListResponse> allData = findPlacesByRegion(regionName);
		return PaginationUtils.getPagedList(allData, pageable);
	}

	@Transactional(readOnly = true)
	public Page<PlaceListResponse> findAllPlacesByMemberId(Pageable pageable) {
		List<PlaceListResponse> allData = findPlacesByRegion("all");
		return PaginationUtils.getPagedList(allData, pageable);
	}
}