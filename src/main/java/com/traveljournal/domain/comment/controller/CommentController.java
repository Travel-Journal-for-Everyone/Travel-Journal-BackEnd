package com.traveljournal.domain.comment.controller;

import java.util.List;

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

import com.traveljournal.domain.comment.dto.CommentCreateRequest;
import com.traveljournal.domain.comment.dto.CommentResponse;
import com.traveljournal.domain.comment.dto.CommentUpdateRequest;
import com.traveljournal.domain.comment.service.CommentService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
@Tag(name = "Comment API", description = "댓글 API")
public class CommentController {

	private final CommentService commentService;

	@Operation(
		summary = "댓글 작성",
		description = "여행일지에 댓글을 작성합니다. parentId가 있으면 대댓글로 작성됩니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping
	public ResponseEntity<CommentResponse> createComment(
		@Valid @RequestBody CommentCreateRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		CommentResponse commentResponse = commentService.createComment(memberId, request);
		return ApiResponseHandler.createdSuccess(commentResponse);
	}

	@Operation(
		summary = "댓글 수정",
		description = "자신이 작성한 댓글을 수정합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PutMapping("/{commentId}")
	public ResponseEntity<CommentResponse> updateComment(
		@PathVariable Long commentId,
		@Valid @RequestBody CommentUpdateRequest request
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		CommentResponse response = commentService.updateComment(memberId, commentId, request);
		return ApiResponseHandler.updatedSuccess(response);
	}

	@Operation(
		summary = "댓글 숨김",
		description = "자신이 작성한 댓글을 숨김 처리합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> hideComment(@PathVariable Long commentId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		commentService.hideComment(memberId, commentId);
		return ApiResponseHandler.createdSuccess("댓글이 숨겨졌습니다.");
	}

	@Operation(
		summary = "여행일지 댓글 목록 조회",
		description = "특정 저널의 댓글 목록을 페이징으로 조회합니다. 대댓글도 함께 조회됩니다."
	)
	@GetMapping("/journal/{journalId}")
	public ResponseEntity<Page<CommentResponse>> getComments(
		@PathVariable Long journalId,
		@PageableDefault(size = 20) Pageable pageable
	) {
		Long memberId = SecurityUtil.getCurrentMemberIdOrNull();
		Page<CommentResponse> comments = commentService.getComments(journalId, memberId, pageable);
		return ApiResponseHandler.getObjectSuccess(comments);
	}

	@GetMapping("/{commentId}/replies")
	@Operation(summary = "댓글의 답글 목록 조회", description = "특정 댓글의 답글들을 조회합니다.")
	public ResponseEntity<List<CommentResponse>> getReplies(@PathVariable Long commentId) {
		Long memberId = SecurityUtil.getCurrentMemberIdOrNull();
		List<CommentResponse> replies = commentService.getReplies(commentId, memberId);
		return ApiResponseHandler.getObjectSuccess(replies);
	}

	@Operation(
		summary = "댓글 상세 조회",
		description = "특정 댓글의 상세 정보를 조회합니다."
	)
	@GetMapping("/{commentId}")
	public ResponseEntity<CommentResponse> getComment(@PathVariable Long commentId) {
		Long memberId = SecurityUtil.getCurrentMemberIdOrNull();
		CommentResponse response = commentService.getComment(commentId, memberId);
		return ApiResponseHandler.getObjectSuccess(response);
	}

	@Operation(
		summary = "댓글 좋아요 토글",
		description = "댓글에 좋아요를 추가하거나 취소합니다. 반환값이 true면 좋아요 추가, false면 취소입니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/{commentId}/like")
	public ResponseEntity<Boolean> toggleLike(@PathVariable Long commentId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		boolean isLiked = commentService.toggleLike(memberId, commentId);
		return ApiResponseHandler.getObjectSuccess(isLiked);
	}
}