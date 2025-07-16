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

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.explore.dto.ExploreJournalFeedResponse;
import com.traveljournal.domain.explore.entity.ExploreSeenJournal;
import com.traveljournal.domain.explore.repository.ExploreSeenJournalRepository;
import com.traveljournal.domain.follow.repository.FollowRepository;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExploreFeedService {

	private final FollowRepository followRepository;
	private final JournalRepository journalRepository;
	private final ExploreSeenJournalRepository exploreSeenJournalRepository;
	private final BlockService blockService;
	private final ImageService imageService;

	@Transactional(readOnly = true)
	public Page<ExploreJournalFeedResponse> getExploreFeed(Long memberId, Pageable pageable) {
		List<Long> followingIds = followRepository.findAcceptedToMemberIdsByFromMemberId(memberId);
		List<Long> blockedIds = blockService.getBlockedMemberIds(memberId);
		List<Long> seenJournalIds = exploreSeenJournalRepository.findSeenJournalIdsByMemberId(memberId);



		if (followingIds != null && !followingIds.isEmpty()) {
			Page<ExploreJournalFeedResponse> followingFeed = getFollowingFeed(
				memberId, followingIds, blockedIds, seenJournalIds, pageable
			);
			if (!followingFeed.isEmpty()) {
				return followingFeed;
			}
		}

		return getRandomFeed(memberId, followingIds, blockedIds, seenJournalIds, pageable);
	}

	private Page<ExploreJournalFeedResponse> getFollowingFeed(
		Long memberId, List<Long> followingIds, List<Long> blockedIds,
		List<Long> seenJournalIds, Pageable pageable) {

		Page<Long> journalIdPage;

		if (hasBlockedMembers(blockedIds) && hasSeenJournals(seenJournalIds)) {
			journalIdPage = journalRepository.findIdsByMemberIdInAndIdNotInExcludingBlocked(
				followingIds, seenJournalIds, blockedIds, pageable
			);
		} else if (hasBlockedMembers(blockedIds)) {
			journalIdPage = journalRepository.findIdsByMemberIdInExcludingBlocked(
				followingIds, blockedIds, pageable
			);
		} else if (hasSeenJournals(seenJournalIds)) {
			journalIdPage = journalRepository.findIdsByMemberIdInAndIdNotInOrderByCreatedAtDesc(
				followingIds, seenJournalIds, pageable
			);
		} else {
			journalIdPage = journalRepository.findIdsByMemberIdInOrderByCreatedAtDesc(
				followingIds, pageable
			);
		}

		return buildFeedResponse(journalIdPage, pageable);
	}

	@Transactional
	public void markJournalsAsSeen(Long memberId, List<Long> journalIds) {
		if (journalIds == null || journalIds.isEmpty()) {
			return;
		}

		List<Long> validJournalIds = journalRepository.findExistingIds(journalIds);
		if (validJournalIds.isEmpty()) {
			return;
		}

		List<Long> alreadySeen = exploreSeenJournalRepository
			.findSeenJournalIdsByMemberIdAndJournalIds(memberId, validJournalIds);

		List<Long> toSave = validJournalIds.stream()
			.filter(id -> !alreadySeen.contains(id))
			.toList();

		if (!toSave.isEmpty()) {
			List<ExploreSeenJournal> entities = toSave.stream()
				.map(journalId -> ExploreSeenJournal.builder()
					.member(Member.builder().id(memberId).build())
					.journal(Journal.builder().id(journalId).build())
					.seenAt(LocalDateTime.now())
					.build())
				.toList();

			exploreSeenJournalRepository.saveAll(entities);
			exploreSeenJournalRepository.flush();
		}
	}

	private boolean hasBlockedMembers(List<Long> blockedIds) {
		return blockedIds != null && !blockedIds.isEmpty();
	}

	private boolean hasSeenJournals(List<Long> seenJournalIds) {
		return seenJournalIds != null && !seenJournalIds.isEmpty();
	}

	private Page<ExploreJournalFeedResponse> buildFeedResponse(
		Page<Long> journalIdPage, Pageable pageable) {

		if (journalIdPage.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0);
		}

		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIdPage.getContent());
		Map<Long, Journal> journalMap = journals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));

		List<ExploreJournalFeedResponse> content = journalIdPage.getContent().stream()
			.map(journalMap::get)
			.filter(Objects::nonNull)
			.map(j -> ExploreJournalFeedResponse.of(j, j.getMember(), imageService))
			.toList();

		return new PageImpl<>(content, pageable, journalIdPage.getTotalElements());
	}

	private Page<ExploreJournalFeedResponse> getRandomFeed(
		Long memberId, List<Long> followingIds, List<Long> blockedIds,
		List<Long> seenJournalIds, Pageable pageable) {

		int limit = pageable.getPageSize();
		boolean hasBlockedMembers = hasBlockedMembers(blockedIds);
		boolean hasFollowing = followingIds != null && !followingIds.isEmpty();
		boolean hasSeenJournals = hasSeenJournals(seenJournalIds);

		List<Long> excludeMemberIds = buildExcludeMemberIds(memberId, blockedIds, followingIds, hasBlockedMembers, hasFollowing);

		if (excludeMemberIds.isEmpty()) {
			excludeMemberIds = List.of(-1L);
		}

		List<Long> randomJournalIds = getRandomJournalIds(excludeMemberIds, seenJournalIds, hasSeenJournals, limit);

		if (randomJournalIds.isEmpty()) {
			return new PageImpl<>(List.of(), pageable, 0);
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
			.map(j -> ExploreJournalFeedResponse.of(j, j.getMember(), imageService))
			.toList();

		long totalElements = getTotalElementsCount(excludeMemberIds, seenJournalIds, hasSeenJournals);

		return new PageImpl<>(content, pageable, totalElements);
	}

	private List<Long> buildExcludeMemberIds(Long memberId, List<Long> blockedIds, List<Long> followingIds,
		boolean hasBlockedMembers, boolean hasFollowing) {

		List<Long> excludeMemberIds = new ArrayList<>();
		excludeMemberIds.add(memberId);

		if (hasBlockedMembers) {
			excludeMemberIds.addAll(blockedIds);
		}
		if (hasFollowing) {
			excludeMemberIds.addAll(followingIds);
		}

		return excludeMemberIds.stream().distinct().toList();
	}

	private List<Long> getRandomJournalIds(List<Long> excludeMemberIds, List<Long> seenJournalIds,
		boolean hasSeenJournals, int limit) {

		if (!hasSeenJournals) {
			return journalRepository.findRandomIdsByMemberIdNotIn(excludeMemberIds, limit);
		} else {
			if (seenJournalIds.isEmpty()) {
				seenJournalIds = List.of(-1L);
			}

			return journalRepository.findRandomIdsByMemberIdNotInAndIdNotIn(excludeMemberIds, seenJournalIds, limit);
		}
	}

	private long getTotalElementsCount(List<Long> excludeMemberIds, List<Long> seenJournalIds,
		boolean hasSeenJournals) {
		if (!hasSeenJournals) {
			return journalRepository.countAvailableJournalsForRandomFeedWithoutSeen(excludeMemberIds);
		} else {
			if (seenJournalIds.isEmpty()) {
				seenJournalIds = List.of(-1L);
			}
			return journalRepository.countAvailableJournalsForRandomFeedWithSeen(excludeMemberIds, seenJournalIds);
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
