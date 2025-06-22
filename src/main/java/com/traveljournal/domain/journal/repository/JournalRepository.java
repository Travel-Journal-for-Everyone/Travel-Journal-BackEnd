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

	@Query("SELECT j.id FROM Journal j WHERE j.member.id IN :memberIds ORDER BY j.createdAt DESC")
	Page<Long> findIdsByMemberIdInOrderByCreatedAtDesc(@Param("memberIds") List<Long> memberIds, Pageable pageable);

	@Query("SELECT j.id FROM Journal j WHERE j.member.id IN :memberIds AND j.id NOT IN :excludeJournalIds ORDER BY j.createdAt DESC")
	Page<Long> findIdsByMemberIdInAndIdNotInOrderByCreatedAtDesc(@Param("memberIds") List<Long> memberIds, @Param("excludeJournalIds") List<Long> excludeJournalIds, Pageable pageable);

	@Query(value = "SELECT j.id FROM journal j WHERE j.member_id NOT IN (:memberIds) AND j.id NOT IN (:excludeJournalIds) ORDER BY j.random_index LIMIT :limit", nativeQuery = true)
	List<Long> findRandomIdsByMemberIdNotInAndIdNotIn(
		@Param("memberIds") List<Long> memberIds,
		@Param("excludeJournalIds") List<Long> excludeJournalIds,
		@Param("limit") int limit
	);

	@Query("""
    SELECT DISTINCT j.id
    FROM Journal j
    LEFT JOIN j.hashTags h
    WHERE (j.title LIKE %:keyword%
       OR j.region LIKE %:keyword%
       OR h.tagName LIKE %:keyword%) AND j.member.id NOT IN :blockedMemberIds
    """)
	Page<Long> findIdsByKeywordExcludingBlockedMembers(@Param("keyword") String keyword, @Param("blockedMemberIds") List<Long> blockedMemberIds, Pageable pageable);

	@Query(value = "SELECT j.id FROM journal j ORDER BY j.random_index LIMIT :limit", nativeQuery = true)
	List<Long> findRandomAll(@Param("limit") int limit);

	@Query(value = """
    SELECT j.id FROM journal j 
    WHERE j.member_id NOT IN (:memberIds) AND j.id NOT IN (:excludeJournalIds) 
    AND j.id >= (SELECT FLOOR(RAND() * (SELECT MAX(id) FROM journal)))
    ORDER BY j.id
    LIMIT :limit
    """, nativeQuery = true)
	List<Long> findOptimizedRandomIdsByMemberIdNotIn(
		@Param("memberIds") List<Long> memberIds,
		@Param("excludeJournalIds") List<Long> excludeJournalIds,
		@Param("limit") int limit
	);

	@Query(value = """
    SELECT j.id FROM journal j 
    WHERE j.id >= (SELECT FLOOR(RAND() * (SELECT MAX(id) FROM journal)))
    ORDER BY j.id
    LIMIT :limit
    """, nativeQuery = true)
	List<Long> findOptimizedRandomAll(@Param("limit") int limit);

	@Query("""
    SELECT j.id FROM Journal j
    WHERE j.member.id = :memberId
      AND j.region IN :regions
      AND (:blockedIds IS NULL OR j.member.id NOT IN :blockedIds)
    """)
	Page<Long> findIdsByMemberIdAndRegionInExcludingBlocked(@Param("memberId") Long memberId, @Param("regions") List<String> regions, @Param("blockedIds") List<Long> blockedIds, Pageable pageable);

	@Query("""
    SELECT j.id FROM Journal j
    WHERE j.member.id = :memberId
      AND (:blockedIds IS NULL OR j.member.id NOT IN :blockedIds)
    """)
	Page<Long> findIdsByMemberIdExcludingBlocked(@Param("memberId") Long memberId, @Param("blockedIds") List<Long> blockedIds, Pageable pageable);

	@Query("SELECT j.region, COUNT(j) FROM Journal j WHERE j.member.id = :memberId GROUP BY j.region")
	List<Object[]> countJournalsByRegion(@Param("memberId") Long memberId);
}