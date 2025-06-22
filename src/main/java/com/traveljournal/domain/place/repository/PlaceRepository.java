package com.traveljournal.domain.place.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.place.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {
	// 1단계: id만 페이징으로 조회 Place
	@Query("SELECT p.id FROM Place p WHERE p.member.id = :memberId AND p.region IN :regions")
	Page<Long> findIdsByMemberIdAndRegionIn(@Param("memberId") Long memberId, @Param("regions") List<String> regions, Pageable pageable);

	// 1단계: id만 페이징으로 검색 PlaceSearch
	@Query("SELECT p.id FROM Place p WHERE p.title LIKE %:keyword% OR p.region LIKE %:keyword%")
	Page<Long> findIdsByTitleOrRegionContaining(@Param("keyword") String keyword, Pageable pageable);

	// 2단계: 실제 데이터 조회
	@Query("SELECT p FROM Place p WHERE p.id IN :ids")
	List<Place> findAllByIdIn(@Param("ids") List<Long> ids);

	// 전체 조회 (단일 회원)
	@Query("SELECT p.id FROM Place p WHERE p.member.id = :memberId")
	Page<Long> findIdsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	@Query("""
		SELECT p.id
		FROM Place p
		WHERE (p.title LIKE %:keyword% OR p.region LIKE %:keyword%) AND p.member.id NOT IN :blockedMemberIds""")
	Page<Long> findIdsByKeywordExcludingBlockedMembers(@Param("keyword") String keyword,@Param("blockedMemberIds") List<Long> blockedMemberIds, Pageable pageable);

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

	@Query("SELECT p.region, COUNT(p) FROM Place p WHERE p.member.id = :memberId GROUP BY p.region")
	List<Object[]> countPlacesByRegion(@Param("memberId") Long memberId);
}