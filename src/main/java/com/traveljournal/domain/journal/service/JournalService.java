package com.traveljournal.domain.journal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.global.dummy.DummyDataProvider;
import com.traveljournal.global.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {
	private final DummyDataProvider dummyDataProvider;

	@Transactional(readOnly = true)
	public List<JournalListResponse> findJournalsByRegion(String regionName) {
		return dummyDataProvider.getDummyJournalsByRegion(regionName);
	}

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findJournalsByRegionWithPaging(String regionName, Pageable pageable) {
		List<JournalListResponse> allData = findJournalsByRegion(regionName);
		return PaginationUtils.getPagedList(allData, pageable);
	}

	// MemberId추후 구현 필요
	@Transactional(readOnly = true)
	public Page<JournalListResponse> findAllJournalsByMemberId(Pageable pageable) {
		List<JournalListResponse> allData = findJournalsByRegion("all");
		return PaginationUtils.getPagedList(allData, pageable);
	}
}