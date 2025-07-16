package com.traveljournal.domain.journal.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.journal.dto.JournalCreateRequest;
import com.traveljournal.domain.journal.dto.JournalDetailResponse;
import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.dto.JournalUpdateRequest;
import com.traveljournal.domain.journal.service.JournalService;
import com.traveljournal.domain.photo.dto.PhotoListResponse;
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
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		return ApiResponseHandler.getObjectSuccess(
			journalService.findJournalsByRegionWithPaging(memberId, currentMemberId, regionName, pageable));
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
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		return ApiResponseHandler.getObjectSuccess(
			journalService.findAllJournalsByMemberId(memberId, currentMemberId, pageable));
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

	@GetMapping("/members/journals/{journalId}")
	@Operation(
		summary = "여행일지 상세 조회",
		description = "특정 여행일지의 상세 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	public ResponseEntity<JournalDetailResponse> getJournalDetail(
		@Parameter(description = "조회할 journal_id")
		@PathVariable Long journalId
	) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();

		return ApiResponseHandler.getObjectSuccess(journalService.getJournalDetail(journalId, currentMemberId));
	}

	@GetMapping("/members/journals/{journalId}/photos")
	@Operation(
		summary = "여행일지 사진 조회",
		description = "특정 여행일지의 모든 사진을 조회합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	public ResponseEntity<List<PhotoListResponse>> getJournalPhotos(
		@Parameter(description = "여행일지 ID", required = true)
		@PathVariable Long journalId) {

		Long memberId = SecurityUtil.getCurrentMemberId();
		return ApiResponseHandler.getObjectSuccess(journalService.getJournalPhotos(journalId, memberId));
	}

	@GetMapping("/members/journals/{journalId}/days/{dayNumber}/photos")
	@Operation(
		summary = "여행일지 특정 일차 사진 조회",
		description = "특정 여행일지의 특정 일차 사진들을 조회합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	public ResponseEntity<List<PhotoListResponse>> getDayPhotos(
		@Parameter(description = "여행일지 ID", required = true)
		@PathVariable Long journalId,
		@Parameter(description = "일차 (1일차, 2일차...)", required = true)
		@PathVariable Integer dayNumber) {

		Long memberId = SecurityUtil.getCurrentMemberId();
		return ApiResponseHandler.getObjectSuccess(journalService.getDayPhotos(journalId, dayNumber, memberId));
	}

	@PutMapping("/members/journals/{journalId}")
	@Operation(
		summary = "여행일지 수정",
		description = "기존 여행일지의 정보를 수정합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses(@ApiResponse(
		responseCode = "200",
		description = "수정 성공",
		content = @Content(mediaType = "text/plain",
			examples = @ExampleObject(name = "여행일지 수정 성공", value = "여행일지 수정이 완료되었습니다."))
	))
	public ResponseEntity<?> updateJournal(
		@Parameter(description = "수정할 journal_id", required = true)
		@PathVariable Long journalId,
		@RequestBody @Valid JournalUpdateRequest journalUpdateRequest
	) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		journalService.updateJournal(journalId, journalUpdateRequest, currentMemberId);
		return ApiResponseHandler.onSuccess("여행일지 수정이 완료되었습니다.");
	}

	@GetMapping("/members/journals/{journalId}/edit")
	@Operation(
		summary = "여행일지 수정 폼 조회",
		description = "여행일지 수정을 위한 기존 데이터를 조회합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	public ResponseEntity<JournalUpdateRequest> getJournalForUpdate(
		@Parameter(description = "수정할 journal_id", required = true)
		@PathVariable Long journalId
	) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		return ApiResponseHandler.getObjectSuccess(
			journalService.getJournalForUpdate(journalId, currentMemberId)
		);
	}

	@DeleteMapping("/members/journals/{journalId}")
	@Operation(
		summary = "여행일지 삭제",
		description = "여행일지를 삭제합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses(@ApiResponse(
		responseCode = "200",
		description = "삭제 성공",
		content = @Content(mediaType = "text/plain",
			examples = @ExampleObject(name = "여행일지 삭제 성공", value = "여행일지가 삭제되었습니다."))
	))
	public ResponseEntity<?> deleteJournal(
		@Parameter(description = "삭제할 journal_id", required = true)
		@PathVariable Long journalId
	) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		journalService.deleteJournal(journalId, currentMemberId);
		return ApiResponseHandler.onSuccess("여행일지가 삭제되었습니다.");
	}
}