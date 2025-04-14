package com.traveljournal.domain.place.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.place.dto.PlaceListResponse;
import com.traveljournal.domain.place.service.PlaceService;
import com.traveljournal.global.data.ApiResponseHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
@Tag(name = "Place API")
public class PlaceController {

	private final PlaceService placeService;

	@Operation(
		summary = "Place Region",
		description = "특정 회원의 행정구역 클릭 시 플레이스 탭 리스트입니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@GetMapping("/members/{memberId}/places/region/{regionName}")
	public ResponseEntity<Page<PlaceListResponse>> getPlacesByRegionPaged(
		@PathVariable Long memberId,
		@PathVariable String regionName,
		@PageableDefault Pageable pageable) {
		return ApiResponseHandler.getObjectSuccess(placeService.getPlacesByRegionWithPagion(regionName, pageable));
	}
}
