package com.traveljournal.domain.journal.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.service.JournalService;
import com.traveljournal.global.data.ApiResponseHandler;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/members/{memberId}/journals")
@RequiredArgsConstructor
public class JournalController {

	private final JournalService journalService;

	@GetMapping("/region/{regionName}")
	public ResponseEntity<Page<JournalListResponse>> getJournalsByRegionPaged(
		@PathVariable String memberId,
		@PathVariable String regionName,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size) {
		return ApiResponseHandler.getObjectSuccess(journalService.getJournalsByRegionWithPaging(regionName, page, size));
	}
}
