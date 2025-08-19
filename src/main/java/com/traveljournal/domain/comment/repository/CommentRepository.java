package com.traveljournal.domain.comment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.comment.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("SELECT c FROM Comment c WHERE c.journal.id = :journalId AND c.parent IS NULL AND c.hidden = false ORDER BY c.createdAt ASC")
	Page<Comment> findTopLevelCommentsByJournalId(@Param("journalId") Long journalId, Pageable pageable);

	@Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND c.hidden = false ORDER BY c.createdAt ASC")
	List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);
}