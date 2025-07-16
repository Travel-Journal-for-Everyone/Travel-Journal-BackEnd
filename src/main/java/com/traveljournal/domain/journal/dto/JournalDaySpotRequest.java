package com.traveljournal.domain.journal.dto;

import com.traveljournal.domain.journal.entity.JournalDaySpot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JournalDaySpotRequest(
	@Schema(example = "1")
	@NotNull(message = "방문지 순서는 필수입니다.")
	@Min(value = 1, message = "방문지 순서는 1 이상이어야 합니다.")
	Integer spotOrder,

	@Schema(example = "경복궁")
	@NotBlank(message = "방문지명은 필수입니다.")
	String spotName,

	@Schema(example = "37.568875000000006")
	@NotNull(message = "위도는 필수입니다.")
	Double latitude,

	@Schema(example = "126.82173888888889")
	@NotNull(message = "경도는 필수입니다.")
	Double longitude
) {
	public static JournalDaySpotRequest from(JournalDaySpot spot) {
		return new JournalDaySpotRequest(
			spot.getSpotOrder(),
			spot.getSpotName(),
			spot.getLatitude(),
			spot.getLongitude()
		);
	}
}