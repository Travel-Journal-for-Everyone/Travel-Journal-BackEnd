package com.traveljournal.domain.memberDashboard.dto;

public record RegionInfo(
	String regionName,
	Long travelDiaryCount,
	Long placesCount
) {
}
