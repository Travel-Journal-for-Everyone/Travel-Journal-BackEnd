package com.traveljournal.domain.journal.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.global.util.RegionGroupUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {

	private final JournalRepository journalRepository;

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findJournalsByRegionWithPaging(Long memberId, String regionName, Pageable pageable) {
		List<String> regionList = RegionGroupUtil.getRegionList(regionName);
		Page<Long> journalIdPage = journalRepository.findIdsByMemberIdAndRegionIn(memberId, regionList, pageable);
		return getJournalListResponses(pageable, journalIdPage);
	}

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findAllJournalsByMemberId(Long memberId, Pageable pageable) {
		Page<Long> journalIdPage = journalRepository.findIdsByMemberId(memberId, pageable);
		return getJournalListResponses(pageable, journalIdPage);
	}

	private Page<JournalListResponse> getJournalListResponses(Pageable pageable, Page<Long> journalIdPage) {
		List<Long> journalIds = journalIdPage.getContent();

		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIds);

		Map<Long, Journal> journalMap = journals.stream().collect(Collectors.toMap(Journal::getId, j -> j));
		List<Journal> sortedJournals = journalIds.stream().map(journalMap::get).toList();

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
