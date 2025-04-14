package com.traveljournal.domain.place.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.global.dummy.DummyDataProvider;
import com.traveljournal.global.security.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

	private final DummyDataProvider dummyDataProvider;

	private List<PlaceListResponse> getPlacesByRegion(String regionName) {
		return dummyDataProvider.getDummyPlacesByRegion(regionName);
	}

	public Page<PlaceListResponse> getPlacesByRegionWithPagion(String regionName, int page, int size) {
		List<PlaceListResponse> allData = getPlacesByRegion(regionName);
		return PaginationUtils.getPagedList(allData, page, size);
	}
}
