package com.traveljournal.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;

public record ReissueRequest (
	@NotEmpty(message = "갱신 토큰은 필수 값입니다.")
	String refreshToken,
	@NotEmpty(message = "기기 아이디는 필수 값입니다.")
	String deviceId
){
}
