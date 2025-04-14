package com.traveljournal.domain.place.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.global.dummy.DummyDataProvider;
import com.traveljournal.global.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final DummyDataProvider dummyDataProvider;

	private List<PlaceListResponse> getPlacesByRegion(String regionName) {
		return dummyDataProvider.getDummyPlacesByRegion(regionName);
	}

	public Page<PlaceListResponse> getPlacesByRegionWithPagion(String regionName, Pageable pageable) {
		List<PlaceListResponse> allData = getPlacesByRegion(regionName);
		return PaginationUtils.getPagedList(allData, pageable);
	}
}
