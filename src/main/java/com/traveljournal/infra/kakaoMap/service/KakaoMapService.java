package com.traveljournal.infra.kakaoMap.service;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.kakaoMap.port.GeoAddressLookupPort;
import com.traveljournal.global.config.KakaoOAuthConfig;
import com.traveljournal.global.exception.ExternalApiException;
import com.traveljournal.infra.kakaoMap.dto.KakaoMapResponse;
import com.traveljournal.infra.kakaoMap.util.KakaoMapFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoMapService implements GeoAddressLookupPort {
	private final KakaoMapFeignClient kakaoMapFeignClient;
	private final KakaoOAuthConfig kakaoOAuthConfig;

	public String getAddress(double latitude, double longitude) {
		try {
			String authorization = "KakaoAK " + kakaoOAuthConfig.getAdminKey();
			KakaoMapResponse kakaoMapResponse = kakaoMapFeignClient.coord2address(authorization, longitude, latitude);
			if (kakaoMapResponse != null
				&& kakaoMapResponse.getDocuments() != null
				&& !kakaoMapResponse.getDocuments().isEmpty()
				&& kakaoMapResponse.getDocuments().get(0).getAddress() != null) {
				return kakaoMapResponse.getDocuments().get(0).getAddress().getAddressName();
			}
		} catch (Exception e) {
			log.warn("카카오맵 역지오코딩 실패: {}", e.getMessage());
			throw new ExternalApiException("카카오맵 역지오코딩 중 오류가 발생했습니다.");
		}
		return null;
	}
}
