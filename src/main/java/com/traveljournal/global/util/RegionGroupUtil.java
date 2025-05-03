package com.traveljournal.global.util;

import java.util.List;
import java.util.Map;

public class RegionGroupUtil {

	public static final Map<String, List<String>> REGION_GROUP_MAP = Map.of(
		"수도권", List.of("서울", "경기", "인천", "경기도"),
		"강원도", List.of("강원도"),
		"충청도", List.of("충청북도", "충청남도", "충북", "충남", "충청도"),
		"전라도", List.of("전라북도", "전라남도", "전북", "전남", "전라도"),
		"제주도", List.of("제주도", "제주"),
		"경상도", List.of("경상북도", "경상남도", "경남", "경북", "경상도")
	);

	public static List<String> getRegionList(String regionGroup) {
		return REGION_GROUP_MAP.getOrDefault(regionGroup, List.of(regionGroup));
	}
}