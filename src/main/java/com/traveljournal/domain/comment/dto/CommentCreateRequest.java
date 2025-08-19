package com.traveljournal.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CommentCreateRequest(
	@NotNull(message = "저널 ID는 필수입니다.")
	@Schema(description = "댓글을 작성할 저널 ID", example = "1")
	Long journalId,

	@NotBlank(message = "댓글 내용은 필수입니다.")
	@Size(max = 1000, message = "댓글은 1000자를 초과할 수 없습니다.")
	@Schema(description = "댓글 내용", example = "정말 멋진 여행이네요!")
	String content,

	@Schema(description = "부모 댓글 ID (대댓글인 경우)", example = "1")
	Long parentId
) {
	public static CommentCreateRequest of(Long journalId, String content, Long parentId) {
		return new CommentCreateRequest(journalId, content, parentId);
	}
}