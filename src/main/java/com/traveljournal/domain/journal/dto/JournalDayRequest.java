package com.traveljournal.domain.journal.dto;

import java.util.List;

import com.traveljournal.domain.journal.entity.JournalDay;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalDayRequest(
	@Schema(example = "1")
	int dayNumber,
	@Schema(example = "설명")
	String description,
	List<JournalDaySpotRequest> journalDaySpots
) {
	public static JournalDayRequest from(JournalDay journalDay) {
		return new JournalDayRequest(
			journalDay.getDayNumber(),
			journalDay.getDescription(),
			journalDay.getSpots().stream()
				.map(JournalDaySpotRequest::from)
				.toList()
		);
	}
}
