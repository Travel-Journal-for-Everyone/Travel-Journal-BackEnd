package com.traveljournal.domain.comment.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.UnauthorizedException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comment",
	indexes = {
		@Index(name = "idx_comment_journal_parent", columnList = "journal_id, parent_id"),
		@Index(name = "idx_comment_journal_created", columnList = "journal_id, createdAt")
	}
)
public class Comment {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "journal_id")
	private Journal journal;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id")
	private Member author;

	@Column(nullable = false, length = 1000)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id")
	private Comment parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("createdAt ASC")
	private List<Comment> children = new ArrayList<>();

	@Column(nullable = false)
	private Long likeCount = 0L;

	@Column(nullable = false)
	private boolean hidden = false;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	@Builder
	public Comment(Journal journal, Member author, String content, Comment parent) {
		this.journal = Objects.requireNonNull(journal, "여행일지는 필수입니다.");
		this.author = Objects.requireNonNull(author, "작성자는 필수입니다.");
		this.content = validateAndTrimContent(content);

		if (parent != null) {
			validateParentComment(parent, journal);
		}

		this.parent = parent;
	}

	public void editContent(String newContent, Long authorId) {
		validateOwnership(authorId);
		this.content = validateAndTrimContent(newContent);
	}

	public void hide(Long requesterId) {
		validateOwnership(requesterId);
		this.hidden = true;
	}

	public void incrementLike() {
		this.likeCount++;
	}

	public void decrementLike() {
		if (this.likeCount > 0) this.likeCount--;
	}

	public boolean isTopLevel() {
		return this.parent == null;
	}

	public boolean isReply() {
		return this.parent != null;
	}

	public int getVisibleChildrenCount() {
		return (int) this.children.stream()
			.filter(child -> !child.hidden)
			.count();
	}

	public void validateReplyTarget() {
		if (this.hidden) {
			throw new BadRequestException("숨겨진 댓글에는 답글을 달 수 없습니다.");
		}

		if (this.parent != null) {
			throw new BadRequestException("대댓글에는 답글을 달 수 없습니다.");
		}
	}

	private void validateOwnership(Long requesterId) {
		if (!Objects.equals(this.author.getId(), requesterId)) {
			throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
		}
	}

	private String validateAndTrimContent(String content) {
		if (content == null || content.trim().isEmpty()) {
			throw new BadRequestException("댓글 내용은 필수입니다.");
		}

		String trimmed = content.trim();
		if (trimmed.length() > 1000) {
			throw new BadRequestException("댓글은 1000자를 초과할 수 없습니다.");
		}

		return trimmed;
	}

	private void validateParentComment(Comment parent, Journal journal) {
		if (!Objects.equals(parent.getJournal().getId(), journal.getId())) {
			throw new IllegalArgumentException("부모 댓글은 동일한 저널에 속해야 합니다.");
		}

		parent.validateReplyTarget();
	}
}