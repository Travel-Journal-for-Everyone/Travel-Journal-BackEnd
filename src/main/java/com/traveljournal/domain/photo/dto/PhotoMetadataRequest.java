package com.traveljournal.domain.photo.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.traveljournal.domain.photo.entity.Photo;
import com.traveljournal.global.exception.BadRequestException;

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
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

	public static PhotoMetadataRequest from(Photo photo) {
		return new PhotoMetadataRequest(
			photo.getImageInfo().getUploadId(),
			photo.getJournalDay().getDayNumber(),
			photo.getDescription(),
			photo.getImageInfo().getUploadFilename(),
			formatDateTime(photo.getTakenDateTime()),
			photo.getAddress(),
			photo.getLatitude(),
			photo.getLongitude()
		);
	}

	@JsonIgnore
	public LocalDateTime getParsedTakenDateTime() {
		if (takenDateTime == null || takenDateTime.trim().isEmpty()) {
			return null;
		}
		try {
			return LocalDateTime.parse(takenDateTime, DATE_TIME_FORMATTER);
		} catch (Exception e) {
			throw new BadRequestException("잘못된 날짜 형식입니다: " + takenDateTime);
		}
	}

	private static String formatDateTime(LocalDateTime dateTime) {
		return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : null;
	}
}