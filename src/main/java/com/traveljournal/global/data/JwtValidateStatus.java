package com.traveljournal.global.data;

public enum JwtValidateStatus {
	ACCEPTED,          // 정상
	EXPIRED,           // 만료됨
	INVALID,           // 변조/서명 오류 등 유효하지 않음
	EMPTY,             // 토큰 없음
	UNSUPPORTED,       // 지원하지 않는 토큰 형식
	ERROR              // 기타 예외 (파싱 실패 등)
}
