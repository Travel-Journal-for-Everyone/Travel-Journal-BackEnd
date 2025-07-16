package com.traveljournal.domain.journal.dto;

import java.util.List;

import com.traveljournal.domain.journal.entity.JournalDay;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record JournalDayRequest(
	@Schema(example = "1")
	@NotNull(message = "일차 번호는 필수입니다.")
	@Min(value = 1, message = "일차 번호는 1 이상이어야 합니다.")
	Integer dayNumber,

	@Schema(example = "설명")
	String description,

	@NotNull(message = "방문지 정보는 필수입니다.")
	@Valid
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
