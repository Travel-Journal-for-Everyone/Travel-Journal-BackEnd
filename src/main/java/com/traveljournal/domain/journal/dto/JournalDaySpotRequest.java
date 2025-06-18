package com.traveljournal.domain.journal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalDaySpotRequest(
	@Schema(example = "1")
	int spotOrder,
	@Schema(example = "경복궁")
	String spotName,
	@Schema(example = "37.568875000000006")
	Double latitude,
	@Schema(example = "126.82173888888889")
	Double longitude
) {
}