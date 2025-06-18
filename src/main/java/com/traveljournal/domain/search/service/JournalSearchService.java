package com.traveljournal.domain.search.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.block.repository.BlockRepository;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.global.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalSearchService {
	private final JournalRepository journalRepository;
	private final BlockRepository blockRepository;

	@Transactional(readOnly = true)
	public Page<JournalListResponse> searchJournals(String keyword, Pageable pageable) {

		Page<Long> journalIdPage = journalRepository.findIdsByTitleOrRegionOrHashTagContaining(keyword, pageable);
		List<Long> journalIds = journalIdPage.getContent();

		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIds);

		Map<Long, Journal> journalMap = journals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));
		List<Journal> sortedJournals = journalIds.stream()
			.map(journalMap::get)
			.toList();

		return new PageImpl<>(
			sortedJournals.stream()
				.map(journal -> new JournalListResponse(
					journal.getId(),
					journal.getHashTags().stream().map(HashTag::getTagName).toList(),
					journal.getTitle(),
					journal.getNights(),
					journal.getDays(),
					journal.getStartDate(),
					journal.getEndDate()
				))
				.toList(),
			pageable,
			journalIdPage.getTotalElements()
		);
	}

	@Transactional(readOnly = true)
	public Page<JournalListResponse> searchJournalsByBlockedMembers(String keyword, Pageable pageable) {

		Long currentMemberId = SecurityUtil.getCurrentMemberId();
		List<Long> blockedIds = blockRepository.findBlockedMemberIdsByBlockerId(currentMemberId);

		Page<Long> journalIdPage = journalRepository.findIdsByKeywordExcludingBlockedMembers(keyword, blockedIds,
			pageable);
		List<Long> journalIds = journalIdPage.getContent();
		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIds);

		Map<Long, Journal> journalMap = journals.stream()
			.collect(Collectors.toMap(Journal::getId, j -> j));
		List<Journal> sortedJournals = journalIds.stream()
			.map(journalMap::get)
			.toList();

		return new PageImpl<>(
			sortedJournals.stream()
				.map(journal -> new JournalListResponse(
					journal.getId(),
					journal.getHashTags().stream().map(HashTag::getTagName).toList(),
					journal.getTitle(),
					journal.getNights(),
					journal.getDays(),
					journal.getStartDate(),
					journal.getEndDate()
				))
				.toList(),
			pageable,
			journalIdPage.getTotalElements()
		);
	}
}
