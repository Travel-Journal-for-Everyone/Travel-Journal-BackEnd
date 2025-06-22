package com.traveljournal.domain.memberDashboard.dto;

import lombok.Builder;

@Builder
public record RegionInfo(
	String regionName,
	Long travelDiaryCount,
	Long placesCount
) {
	public static RegionInfo of(String regionName, Long travelDiaryCount, Long placesCount) {
		return RegionInfo.builder()
			.regionName(regionName)
			.travelDiaryCount(travelDiaryCount)
			.placesCount(placesCount)
			.build();
	}
}
