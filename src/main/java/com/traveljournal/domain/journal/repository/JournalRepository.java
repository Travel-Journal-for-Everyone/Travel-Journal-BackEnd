package com.traveljournal.domain.journal.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.journal.entity.Journal;

public interface JournalRepository extends JpaRepository<Journal, Long> {

	// 1단계: id만 페이징으로 조회
	@Query("SELECT j.id FROM Journal j WHERE j.member.id = :memberId AND j.region IN :regions")
	Page<Long> findIdsByMemberIdAndRegionIn(@Param("memberId") Long memberId, @Param("regions") List<String> regions, Pageable pageable);

	@Query("""
    SELECT DISTINCT j.id
    FROM Journal j
    LEFT JOIN j.hashTags h
    WHERE j.title LIKE %:keyword%
       OR j.region LIKE %:keyword%
       OR h.tagName LIKE %:keyword%
    """)
	Page<Long> findIdsByTitleOrRegionOrHashTagContaining(@Param("keyword") String keyword, Pageable pageable);

	// 2단계: fetch join으로 실제 데이터 조회
	@Query("SELECT DISTINCT j FROM Journal j LEFT JOIN FETCH j.hashTags WHERE j.id IN :ids")
	List<Journal> findAllByIdInFetchJoin(@Param("ids") List<Long> ids);

	@Query("SELECT j.id FROM Journal j WHERE j.member.id = :memberId")
	Page<Long> findIdsByMemberId(@Param("memberId") Long memberId, Pageable pageable);
}