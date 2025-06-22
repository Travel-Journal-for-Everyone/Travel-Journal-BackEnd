package com.traveljournal.global.util;

import java.util.HashMap;
import java.util.Map;

public class RegionNormalizer {

	private static final Map<String, String> REGION_MAP = new HashMap<>();

	static {
		// 수도권
		REGION_MAP.put("서울", "서울");
		REGION_MAP.put("서울특별시", "서울");
		REGION_MAP.put("서울시", "서울");

		REGION_MAP.put("경기", "경기");
		REGION_MAP.put("경기도", "경기");
		REGION_MAP.put("경기광역시", "경기");

		REGION_MAP.put("인천", "인천");
		REGION_MAP.put("인천광역시", "인천");
		REGION_MAP.put("인천시", "인천");

		// 강원권
		REGION_MAP.put("강원", "강원도");
		REGION_MAP.put("강원도", "강원도");

		// 충청권
		REGION_MAP.put("충청북도", "충청도");
		REGION_MAP.put("충북", "충청도");
		REGION_MAP.put("충청남도", "충청도");
		REGION_MAP.put("충남", "충청도");
		REGION_MAP.put("충청도", "충청도");

		// 전라권
		REGION_MAP.put("전라북도", "전라도");
		REGION_MAP.put("전북", "전라도");
		REGION_MAP.put("전라남도", "전라도");
		REGION_MAP.put("전남", "전라도");
		REGION_MAP.put("전라도", "전라도");

		// 경상권
		REGION_MAP.put("경상북도", "경상도");
		REGION_MAP.put("경북", "경상도");
		REGION_MAP.put("경상남도", "경상도");
		REGION_MAP.put("경남", "경상도");
		REGION_MAP.put("경상도", "경상도");

		// 제주권
		REGION_MAP.put("제주", "제주도");
		REGION_MAP.put("제주도", "제주도");
		REGION_MAP.put("제주특별자치도", "제주도");
	}

	/**
	 * 입력값을 표준화된 지역명으로 변환합니다.
	 * @param rawRegion 입력된 지역명(예: "서울특별시", "경북" 등)
	 * @return 표준화된 지역명(예: "서울", "경상도" 등)
	 */
	public static String normalize(String rawRegion) {
		if (rawRegion == null) return null;
		String trimmed = rawRegion.trim();
		return REGION_MAP.getOrDefault(trimmed, trimmed);
	}
}
