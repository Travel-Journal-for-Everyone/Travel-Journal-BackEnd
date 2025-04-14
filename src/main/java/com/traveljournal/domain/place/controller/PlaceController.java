package com.traveljournal.domain.place.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.domain.place.service.PlaceService;
import com.traveljournal.global.data.ApiResponseHandler;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "Place API")
public class PlaceController {

	private final PlaceService placeService;

	@GetMapping("/members/{memberId}/places/region/{regionName}")
	public ResponseEntity<Page<PlaceListResponse>> getPlacesByRegionPaged(
		@PathVariable Long memberId,
		@PathVariable String regionName,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponseHandler.getObjectSuccess(placeService.getPlacesByRegionWithPagion(regionName, page, size));
	}
}
