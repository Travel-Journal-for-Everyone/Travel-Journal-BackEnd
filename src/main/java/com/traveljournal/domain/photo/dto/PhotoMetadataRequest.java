package com.traveljournal.domain.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PhotoMetadataRequest(
	@Schema(example = "journal_photo_20_1750074767395_cb576cc6.jpeg")
	String uploadId,
	@Schema(example = "1")
	int dayNumber,
	@Schema(example = "설명")
	String description,
	@Schema(example = "원본 파일명")
	String uploadFilename,
	@Schema(example = "2025.06.04 21:27")
	String takenDateTime,
	@Schema(example = "서울 강서구 마곡동 735")
	String address,
	@Schema(example = "37.568875000000006")
	Double latitude,
	@Schema(example = "126.82173888888889")
	Double longitude
) {
}