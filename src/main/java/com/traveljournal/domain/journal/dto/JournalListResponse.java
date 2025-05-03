package com.traveljournal.domain.journal.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalListResponse(
	@Schema(example = "1")
	Long journalId,
	@Schema(example = "[\"서울\", \"여행\", \"도심\"]")
	List<String> hashTag,
	@Schema(example = "서울 도심 속 힐링 명소 탐방기")
	String title,
	@Schema(example = "2")
	Long nights,
	@Schema(example = "3")
	Long days,
	@Schema(example = "2025.03.15")
	String startDate,
	@Schema(example = "2025.03.18")
	String endDate
) {
}
