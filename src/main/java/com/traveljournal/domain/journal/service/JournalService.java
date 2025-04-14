package com.traveljournal.domain.journal.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.global.dummy.DummyDataProvider;
import com.traveljournal.global.util.PaginationUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {
	private final DummyDataProvider dummyDataProvider;

	public List<JournalListResponse> getJournalsByRegion(String regionName) {
		return dummyDataProvider.getDummyJournalsByRegion(regionName);
	}

	public Page<JournalListResponse> getJournalsByRegionWithPaging(String regionName, Pageable pageable) {
		List<JournalListResponse> allData = getJournalsByRegion(regionName);
		return PaginationUtils.getPagedList(allData, pageable);
	}
}