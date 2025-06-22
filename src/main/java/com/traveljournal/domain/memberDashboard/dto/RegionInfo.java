package com.traveljournal.domain.memberDashboard.dto;

public record RegionInfo(
	String regionName,
	Long travelDiaryCount,
	Long placesCount
) {
	public static RegionInfo of(String regionName, Long travelDiaryCount, Long placesCount) {
		return new RegionInfo(regionName, travelDiaryCount, placesCount);
	}
}
