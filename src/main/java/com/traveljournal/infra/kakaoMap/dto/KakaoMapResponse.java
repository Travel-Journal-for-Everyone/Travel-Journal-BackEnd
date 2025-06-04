package com.traveljournal.infra.kakaoMap.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoMapResponse {
	private Meta meta;
	private List<Document> documents;

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Meta {
		@JsonProperty("total_count")
		private int totalCount;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Document {
		private Address address;
		@JsonProperty("road_address")
		private RoadAddress roadAddress;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Address {
		@JsonProperty("address_name")
		private String addressName;
	}

	@Getter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class RoadAddress {
		@JsonProperty("address_name")
		private String addressName;
	}
}