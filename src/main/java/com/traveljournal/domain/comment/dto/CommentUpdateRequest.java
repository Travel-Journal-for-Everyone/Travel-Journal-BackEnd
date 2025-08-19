package com.traveljournal.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

public record CommentUpdateRequest(
	@NotBlank(message = "댓글 내용은 필수입니다.")
	@Size(max = 1000, message = "댓글은 1000자를 초과할 수 없습니다.")
	@Schema(description = "수정할 댓글 내용", example = "수정된 댓글 내용입니다.")
	String content
) {
	public static CommentUpdateRequest of(String content) {
		return new CommentUpdateRequest(content);
	}
}