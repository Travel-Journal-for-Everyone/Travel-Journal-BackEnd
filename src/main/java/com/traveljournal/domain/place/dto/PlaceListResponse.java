package com.traveljournal.domain.place.dto;

public record PlaceListResponse(
	Long placeId,
	String title,
	String region,
	String thumbnailUrl
) {
}
