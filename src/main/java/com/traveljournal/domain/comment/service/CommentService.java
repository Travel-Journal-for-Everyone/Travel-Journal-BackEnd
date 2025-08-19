package com.traveljournal.domain.comment.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.comment.dto.CommentCreateRequest;
import com.traveljournal.domain.comment.dto.CommentResponse;
import com.traveljournal.domain.comment.dto.CommentUpdateRequest;
import com.traveljournal.domain.comment.entity.Comment;
import com.traveljournal.domain.comment.entity.CommentLike;
import com.traveljournal.domain.comment.repository.CommentLikeRepository;
import com.traveljournal.domain.comment.repository.CommentRepository;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.service.JournalService;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final MemberService memberService;
	private final JournalService journalService;

	@Transactional
	public CommentResponse createComment(Long memberId, CommentCreateRequest request) {
		Member author = memberService.findById(memberId);
		Journal journal = journalService.findById(request.journalId());

		Comment parent = null;
		if (request.parentId() != null) {
			parent = findCommentById(request.parentId());
			parent.validateReplyTarget();
		}

		Comment comment = Comment.builder()
			.journal(journal)
			.author(author)
			.content(request.content())
			.parent(parent)
			.build();

		Comment savedComment = commentRepository.save(comment);

		return CommentResponse.from(savedComment, memberId, false);
	}

	@Transactional
	public CommentResponse updateComment(Long memberId, Long commentId, CommentUpdateRequest request) {
		Comment comment = findCommentById(commentId);
		comment.editContent(request.content(), memberId);

		boolean isLiked = commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId);

		return CommentResponse.from(comment, memberId, isLiked);
	}

	@Transactional
	public void hideComment(Long memberId, Long commentId) {
		Comment comment = findCommentById(commentId);
		comment.hide(memberId);
	}

	@Transactional(readOnly = true)
	public Page<CommentResponse> getComments(Long journalId, Long memberId, Pageable pageable) {
		journalService.findById(journalId);

		Page<Comment> comments = commentRepository.findTopLevelCommentsByJournalId(journalId, pageable);

		return comments.map(comment -> {
			boolean isLiked = memberId != null &&
				commentLikeRepository.existsByCommentIdAndMemberId(comment.getId(), memberId);

			return CommentResponse.from(comment, memberId, isLiked);
		});
	}


	@Transactional
	public boolean toggleLike(Long memberId, Long commentId) {
		Comment comment = findCommentById(commentId);
		Member member = memberService.findById(memberId); // Service 호출

		return commentLikeRepository.findByCommentIdAndMemberId(commentId, memberId)
			.map(like -> {
				commentLikeRepository.delete(like);
				comment.decrementLike();
				return false;
			})
			.orElseGet(() -> {
				CommentLike like = CommentLike.of(comment, member);
				commentLikeRepository.save(like);
				comment.incrementLike();
				return true;
			});
	}

	@Transactional(readOnly = true)
	public CommentResponse getComment(Long commentId, Long memberId) {
		Comment comment = findCommentById(commentId);
		boolean isLiked = memberId != null &&
			commentLikeRepository.existsByCommentIdAndMemberId(commentId, memberId);

		return CommentResponse.from(comment, memberId, isLiked);
	}

	@Transactional(readOnly = true)
	public List<CommentResponse> getReplies(Long parentCommentId, Long memberId) {
		Comment parentComment = findCommentById(parentCommentId);

		if (parentComment.isHidden()) {
			throw new BadRequestException("숨겨진 댓글의 답글은 조회할 수 없습니다.");
		}

		List<Comment> replies = commentRepository.findRepliesByParentId(parentCommentId);
		return replies.stream()
			.map(reply -> {
				boolean isLiked = memberId != null &&
					commentLikeRepository.existsByCommentIdAndMemberId(reply.getId(), memberId);
				return CommentResponse.from(reply, memberId, isLiked);
			})
			.toList();
	}

	private Comment findCommentById(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다. ID: " + commentId));
	}
}
