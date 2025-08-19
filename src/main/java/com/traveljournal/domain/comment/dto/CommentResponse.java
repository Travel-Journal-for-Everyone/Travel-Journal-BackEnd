package com.traveljournal.domain.comment.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.traveljournal.domain.comment.entity.Comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CommentResponse(
	@Schema(example = "1")
	Long id,

	@Schema(example = "1")
	Long journalId,

	@Schema(example = "1")
	Long authorId,

	@Schema(example = "도요새")
	String authorNickname,

	@Schema(example = "https://example.com/profile.jpg")
	String authorProfileImageUrl,

	@Schema(example = "정말 멋진 여행이네요!")
	String content,

	@Schema(example = "1")
	Long parentId,

	@Schema(example = "5")
	Long likeCount,

	@Schema(example = "3")
	Integer replyCount,

	@Schema(example = "false")
	Boolean isLiked,

	@Schema(example = "false")
	Boolean isAuthor,

	@Schema(example = "false")
	Boolean isHidden,

	@Schema(description = "댓글 작성 시간", example = "2025-08-14 17:07:00")
	String createdAt,

	@Schema(description = "댓글 수정 시간", example = "2025-08-14 17:07:00")
	String updatedAt
) {
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static CommentResponse from(Comment comment, Long currentMemberId, boolean isLiked) {
		return CommentResponse.builder()
			.id(comment.getId())
			.journalId(comment.getJournal().getId())
			.authorId(comment.getAuthor().getId())
			.authorNickname(comment.getAuthor().getNickname())
			.authorProfileImageUrl(comment.getAuthor().getProfileImageUrl())
			.content(comment.isHidden() ? "숨겨진 댓글입니다." : comment.getContent())
			.parentId(comment.getParent() != null ? comment.getParent().getId() : null)
			.likeCount(comment.getLikeCount())
			.replyCount(comment.getVisibleChildrenCount())
			.isLiked(isLiked)
			.isAuthor(currentMemberId != null && Objects.equals(comment.getAuthor().getId(), currentMemberId))
			.isHidden(comment.isHidden())
			.createdAt(formatDateTime(comment.getCreatedAt()))
			.updatedAt(formatDateTime(comment.getUpdatedAt()))
			.build();
	}

	private static String formatDateTime(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(FORMATTER) : null;
	}
}