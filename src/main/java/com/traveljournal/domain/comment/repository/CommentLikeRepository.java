package com.traveljournal.domain.comment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.comment.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	Optional<CommentLike> findByCommentIdAndMemberId(Long commentId, Long memberId);

	boolean existsByCommentIdAndMemberId(Long commentId, Long memberId);

}