package com.traveljournal.infra.kakaoMap.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.traveljournal.infra.kakaoMap.dto.KakaoMapResponse;

@FeignClient(name = "kakaoMapClient", url = "https://dapi.kakao.com")
public interface KakaoMapFeignClient {

	@GetMapping("/v2/local/geo/coord2address.json")
	KakaoMapResponse coord2address(
		@RequestHeader("Authorization") String authorization,
		@RequestParam("x") double longitude,
		@RequestParam("y") double latitude
	);
}
