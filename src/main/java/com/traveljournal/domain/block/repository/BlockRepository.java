package com.traveljournal.domain.block.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.block.entity.Block;
import com.traveljournal.domain.member.entity.Member;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);
    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);
    Page<Block> findAllByBlocker(Member blocker, Pageable pageable);
    void deleteByBlockerAndBlocked(Member blocker, Member blocked);

    @Query("SELECT b.blocked.id FROM Block b WHERE b.blocker.id = :blockerId")
    List<Long> findBlockedMemberIdsByBlockerId(@Param("blockerId") Long blockerId);

    boolean existsByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    @Query("SELECT b.blocker.id FROM Block b WHERE b.blocked.id = :blockedId")
    List<Long> findBlockerIdsByBlockedId(@Param("blockedId") Long blockedId);
}
