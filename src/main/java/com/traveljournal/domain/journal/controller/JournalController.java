package com.traveljournal.domain.journal.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.journal.dto.JournalCreateRequest;
import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.service.JournalService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Tag(name = "Journal API", description = "여행일지 API")
public class JournalController {

	private final JournalService journalService;

	@Operation(
		summary = "Journal Region",
		description = "특정 회원의 행정구역 클릭 시 여행일지 탭 리스트입니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@GetMapping("/members/{memberId}/journals/region/{regionName}")
	public ResponseEntity<Page<JournalListResponse>> findJournalsByRegionPaged(
		@Parameter(description = "조회할 member_id")
		@PathVariable Long memberId,
		@Parameter(description = "수도권, 강원도, 충정도, 경상도, 전라도, 제주도")
		@PathVariable String regionName,
		@ParameterObject @PageableDefault Pageable pageable) {
		return ApiResponseHandler.getObjectSuccess(
			journalService.findJournalsByRegionWithPaging(memberId, regionName, pageable));
	}

	@Operation(
		summary = "회원의 전체 여행일지 조회",
		description = "특정 회원의 전체 여행일지 리스트를 페이징하여 반환합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@GetMapping("/members/{memberId}/journals")
	public ResponseEntity<Page<JournalListResponse>> findJournalsByMember(
		@Parameter(description = "조회할 member_id")
		@PathVariable Long memberId,
		@ParameterObject @PageableDefault Pageable pageable) {
		return ApiResponseHandler.getObjectSuccess(journalService.findAllJournalsByMemberId(memberId, pageable));
	}

	@PostMapping(value = "/members/journal/create")
	@Operation(
		summary = "여행일지 작성",
		description = "여행일지, 일차, 방문지, 사진 메타데이터를 한 번에 저장합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses(@ApiResponse(
		responseCode = "200",
		description = "성공",
		content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "여행일지 작성 성공", value = "여행일지 작성 성공 journal_id: 1"))
	))
	public ResponseEntity<?> createJournal(
		@RequestBody @Valid JournalCreateRequest journalCreateRequest
	) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();

		Long journalId = journalService.createJournal(journalCreateRequest, currentMemberId);
		return ApiResponseHandler.onSuccess("여행일지 작성 성공 journal_id: " + journalId);
	}
}