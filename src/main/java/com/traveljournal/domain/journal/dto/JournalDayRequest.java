package com.traveljournal.domain.journal.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalDayRequest(
	@Schema(example = "1")
	int dayNumber,
	@Schema(example = "설명")
	String description,
	List<JournalDaySpotRequest> journalDaySpots
) {
}
