package com.traveljournal.domain.kakaoMap.port;

public interface GeoAddressLookupPort {
	String getAddress(double latitude, double longitude);
}