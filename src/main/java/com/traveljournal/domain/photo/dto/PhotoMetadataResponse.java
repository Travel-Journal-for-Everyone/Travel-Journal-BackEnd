package com.traveljournal.domain.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PhotoMetadataResponse(
	@Schema(example = "2025.06.04 21:27")
	String takenDateTime,    // 촬영일시
	@Schema(example = "서울 강서구 마곡동 735")
	String address,          // 주소
	@Schema(example = "37.568875000000006")
	Double latitude,         // 위도
	@Schema(example = "126.82173888888889")
	Double longitude         // 경도
) {
	public static PhotoMetadataResponse of(
		String takenDateTime, String address, Double latitude, Double longitude) {
		return PhotoMetadataResponse.builder()
			.takenDateTime(takenDateTime)
			.address(address)
			.latitude(latitude)
			.longitude(longitude)
			.build();
	}

	public static PhotoMetadataResponse empty() {
		return PhotoMetadataResponse.builder().build();
	}
}