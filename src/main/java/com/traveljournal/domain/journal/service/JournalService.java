package com.traveljournal.domain.journal.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.global.dummy.DummyDataProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {
	private final DummyDataProvider dummyDataProvider;

	public List<JournalListResponse> getJournalsByRegion(String regionName) {
		return dummyDataProvider.getDummyJournalsByRegion(regionName);
	}

	public Page<JournalListResponse> getJournalsByRegionWithPaging(String regionName, int page, int size) {
		List<JournalListResponse> allData = getJournalsByRegion(regionName);

		int start = page * size;
		int end = Math.min(start + size, allData.size());

		if (start > allData.size()) {
			return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), allData.size());
		}

		List<JournalListResponse> pageData = allData.subList(start, end);
		return new PageImpl<>(pageData, PageRequest.of(page, size), allData.size());
	}
}

