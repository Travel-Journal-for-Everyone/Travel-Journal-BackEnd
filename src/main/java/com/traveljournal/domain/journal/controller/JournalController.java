package com.traveljournal.domain.journal.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.service.JournalService;
import com.traveljournal.global.data.ApiResponseHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "journal API", description = "여행일지 API")
public class JournalController {

	private final JournalService journalService;

	@Operation(
		summary = "Journal Region",
		description = "특정 회원의 행정구역 클릭 시 여행일지 탭 리스트입니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@GetMapping("/members/{memberId}/journals/region/{regionName}")
	public ResponseEntity<Page<JournalListResponse>> getJournalsByRegionPaged(
		@PathVariable Long memberId,
		@PathVariable String regionName,
		@PageableDefault Pageable pageable) {
		return ApiResponseHandler.getObjectSuccess(journalService.getJournalsByRegionWithPaging(regionName, pageable));
	}
}
