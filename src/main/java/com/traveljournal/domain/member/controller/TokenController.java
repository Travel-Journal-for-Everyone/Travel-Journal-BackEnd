package com.traveljournal.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.member.dto.MemberTokenResponse;
import com.traveljournal.domain.member.dto.TokenRefreshRequest;
import com.traveljournal.domain.member.service.TokenService;
import com.traveljournal.global.data.ApiResponse;
import com.traveljournal.global.data.ApiResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tokens")
@Tag(name = "token API", description = "토큰 관련 API")
public class TokenController {

	private final TokenService tokenService;

	/**
	 * 토큰 재발급 요청 처리
	 * Refresh 토큰과 장치 ID를 사용하여 새로운 액세스 토큰 발급
	 */
	@Operation(
		summary = "Token Refresh",
		description = "Refresh 토큰과 장치 ID를 사용하여 새로운 액세스 토큰을 발급합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/reissue")
	public ResponseEntity<ApiResult<MemberTokenResponse>> refreshToken(@RequestBody TokenRefreshRequest request) {
		MemberTokenResponse response = tokenService.refreshToken(request);
		return ApiResponse.ok(response);
	}
}
