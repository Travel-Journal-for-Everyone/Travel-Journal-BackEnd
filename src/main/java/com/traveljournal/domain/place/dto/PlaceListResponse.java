package com.traveljournal.domain.place.dto;

import com.traveljournal.domain.place.entity.Place;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record PlaceListResponse(
	@Schema(example = "1")
	Long placeId,
	@Schema(example = "서울 타워")
	String title,
	@Schema(example = "서울")
	String region,
	@Schema(example = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyNTAzMjFfMTEw%2FMDAxNzQyNTU5MjY0OTkz.US8DxCfatYon23fMlPjPlqGIvpK8Zd8SIP3BuNFyGmUg.LUZS2bZBQ1aJuHyVI52EjhzHykDFewCj4mpJCeoV0G0g.JPEG%2F2025%25BA%25A2%25B2%25C9%25B0%25B3%25C8%25AD%25BD%25C3%25B1%25E2IMG_2503-009.JPG&type=sc960_832")
	String thumbnailUrl
) {

	public static PlaceListResponse of(Place place) {
		return PlaceListResponse.builder()
			.placeId(place.getId())
			.title(place.getTitle())
			.region(place.getRegion())
			.thumbnailUrl(place.getThumbnailUrl())
			.build();
	}
}
