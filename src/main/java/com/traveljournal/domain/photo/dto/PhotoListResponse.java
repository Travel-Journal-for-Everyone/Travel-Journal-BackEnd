package com.traveljournal.domain.photo.dto;

import java.time.format.DateTimeFormatter;

import com.traveljournal.domain.photo.entity.Photo;

import io.swagger.v3.oas.annotations.media.Schema;

public record PhotoListResponse(
	@Schema(example = "1")
	Integer photoOrder,
	@Schema(example = "1")
	Integer daySpotOrder,
	@Schema(example = "journal_photo_20_1750242032088_99a07d36.jpeg")
	String uploadId,
	@Schema(example = "https://travel-journal-s3.s3.amazonaws.com/source/journal_photo_20_1750242032088_99a07d36.jpeg")
	String photoUrl,
	@Schema(example = "1")
	int dayNumber,
	@Schema(example = "설명")
	String description,
	@Schema(example = "2025.06.04 21:27")
	String takenDateTime,
	@Schema(example = "서울 강서구 마곡동 735")
	String address,
	@Schema(example = "37.568875000000006")
	Double latitude,
	@Schema(example = "126.82173888888889")
	Double longitude
) {
	public static PhotoListResponse from(Photo photo, String photoUrl) {
		return new PhotoListResponse(
			photo.getPhotoOrder(),
			photo.getDaySpotOrder(),
			photo.getImageInfo().getUploadId(),
			photoUrl,
			photo.getJournalDay().getDayNumber(),
			photo.getDescription(),
			photo.getTakenDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")),
			photo.getAddress(),
			photo.getLatitude(),
			photo.getLongitude()
		);
	}
}
