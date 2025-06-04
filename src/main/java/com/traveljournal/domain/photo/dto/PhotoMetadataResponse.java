package com.traveljournal.domain.photo.dto;

import lombok.Builder;

@Builder
public record PhotoMetadataResponse(
	String takenDateTime,    // 촬영일시
	String address,          // 주소
	Double latitude,         // 위도
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