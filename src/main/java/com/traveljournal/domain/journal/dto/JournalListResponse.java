package com.traveljournal.domain.journal.dto;

import java.util.List;

public record JournalListResponse(
	Long journalId,
	List<String> hashTag,
	String title,
	Long nights,
	Long days,
	String startDate,
	String endDate
) {
}
