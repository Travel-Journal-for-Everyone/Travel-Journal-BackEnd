package com.traveljournal.domain.explore.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.explore.dto.ExploreJournalFeedResponse;
import com.traveljournal.domain.explore.entity.ExploreSeenJournal;
import com.traveljournal.domain.explore.repository.ExploreSeenJournalRepository;
import com.traveljournal.domain.follow.repository.FollowRepository;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExploreFeedService {

	private final FollowRepository followRepository;
	private final JournalRepository journalRepository;
	private final ExploreSeenJournalRepository exploreSeenJournalRepository;
	private final BlockService blockService;

	@Transactional(readOnly = true)
	public Page<ExploreJournalFeedResponse> getExploreFeed(Long memberId, Pageable pageable) {
		List<Long> followingIds = followRepository.findAcceptedToMemberIdsByFromMemberId(memberId);

		List<Long> blockedIds = blockService.getBlockedMemberIds(memberId);
		boolean hasBlockedMembers = blockedIds != null && !blockedIds.isEmpty();

		// 1. 팔로우한 회원이 없으면 바로 랜덤 피드
		if (followingIds == null || followingIds.isEmpty()) {
			return getOptimizedRandomFeed(memberId, List.of(), List.of(), blockedIds, pageable);
		}

		// 2. 이미 본 일지 목록 조회
		List<Long> seenJournalIds = exploreSeenJournalRepository.findSeenJournalIdsByMemberId(memberId);

		// 3. 페이징 최적화: 필요한 ID만 먼저 조회
		Page<Long> journalIdPage;
		if (seenJournalIds == null || seenJournalIds.isEmpty()) {
			if (hasBlockedMembers) {
				journalIdPage = journalRepository.findIdsByMemberIdInExcludingBlocked(followingIds, blockedIds,
					pageable);
			} else {
				journalIdPage = journalRepository.findIdsByMemberIdInOrderByCreatedAtDesc(followingIds, pageable);
			}
		} else {
			if (hasBlockedMembers) {
				journalIdPage = journalRepository.findIdsByMemberIdInAndIdNotInExcludingBlocked(
					followingIds, seenJournalIds, blockedIds, pageable);
			} else {
				journalIdPage = journalRepository.findIdsByMemberIdInAndIdNotInOrderByCreatedAtDesc(
					followingIds, seenJournalIds, pageable);
			}
		}

		// 4. 팔로우한 회원의 읽지 않은 게시글이 없으면 랜덤 피드
		if (journalIdPage.isEmpty()) {
			return getOptimizedRandomFeed(memberId, followingIds, seenJournalIds, blockedIds, pageable);
		}

		// 5. fetch join으로 필요한 데이터만 한 번에 조회
		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIdPage.getContent());

		// 6. ID 기준으로 정렬하여 순서 보장
		Map<Long, Journal> journalMap = journals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));

		List<ExploreJournalFeedResponse> content = journalIdPage.getContent().stream()
			.map(journalMap::get)
			.filter(Objects::nonNull)
			.map(j -> ExploreJournalFeedResponse.of(j, j.getMember()))
			.toList();

		return new PageImpl<>(content, pageable, journalIdPage.getTotalElements());
	}

	private Page<ExploreJournalFeedResponse> getOptimizedRandomFeed(
		Long memberId, List<Long> followingIds, List<Long> seenJournalIds, List<Long> blockedIds, Pageable pageable) {

		int limit = pageable.getPageSize();
		boolean hasBlockedMembers = blockedIds != null && !blockedIds.isEmpty();
		boolean hasFollowing = followingIds != null && !followingIds.isEmpty();
		boolean hasSeenJournals = seenJournalIds != null && !seenJournalIds.isEmpty();

		List<Long> excludeMemberIds = new ArrayList<>();
		excludeMemberIds.add(memberId);
		if (hasBlockedMembers) {
			excludeMemberIds.addAll(blockedIds);
		}
		if (hasFollowing) {
			excludeMemberIds.addAll(followingIds);
		}
		excludeMemberIds = excludeMemberIds.stream().distinct().toList();

		List<Long> randomJournalIds;

		if (!hasSeenJournals) {
			randomJournalIds = journalRepository.findRandomIdsByMemberIdNotIn(excludeMemberIds, limit);
		} else {
			randomJournalIds = journalRepository.findOptimizedRandomIdsByMemberIdNotIn(
				excludeMemberIds, seenJournalIds, limit
			);
		}

		if (randomJournalIds.isEmpty()) {
			if (!hasSeenJournals) {
				randomJournalIds = journalRepository.findRandomIdsByMemberIdNotIn(excludeMemberIds, limit);
			} else {
				randomJournalIds = journalRepository.findOptimizedRandomIdsByMemberIdNotIn(
					excludeMemberIds, seenJournalIds, limit
				);
			}
		}

		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(randomJournalIds);

		Map<Long, Journal> journalMap = journals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));

		List<Journal> orderedJournals = randomJournalIds.stream()
			.map(journalMap::get)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		Collections.shuffle(orderedJournals);

		List<ExploreJournalFeedResponse> content = orderedJournals.stream()
			.map(j -> ExploreJournalFeedResponse.of(j, j.getMember()))
			.toList();

		long totalElements = journalRepository.countAvailableJournalsForRandomFeed(excludeMemberIds,
			hasSeenJournals ? seenJournalIds : List.of());

		return new PageImpl<>(content, pageable, totalElements);
	}

	@Transactional
	public void markJournalsAsSeen(Long memberId, List<Long> journalIds) {
		// 이미 본 일지 필터링하여 불필요한 쿼리 감소
		List<Long> alreadySeen = exploreSeenJournalRepository.findSeenJournalIdsByMemberIdAndJournalIds(memberId,
			journalIds);

		List<Long> toSave = journalIds.stream()
			.filter(id -> !alreadySeen.contains(id))
			.toList();

		if (!toSave.isEmpty()) {
			// 벌크 삽입으로 쿼리 최소화
			List<ExploreSeenJournal> entities = toSave.stream()
				.map(journalId -> ExploreSeenJournal.builder()
					.member(Member.builder().id(memberId).build())
					.journal(Journal.builder().id(journalId).build())
					.seenAt(LocalDateTime.now())
					.build())
				.toList();

			exploreSeenJournalRepository.saveAll(entities);
		}
	}

	@Scheduled(cron = "0 0 3 * * *")
	@Transactional
	public void deleteOldSeenJournals() {
		// 주기적으로 오래된 데이터 정리
		LocalDateTime cutoff = LocalDateTime.now().minusDays(3);
		exploreSeenJournalRepository.deleteAllBySeenAtBefore(cutoff);
	}

	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void refreshRandomIndex() {
		journalRepository.updateRandomIndex();
	}
}
