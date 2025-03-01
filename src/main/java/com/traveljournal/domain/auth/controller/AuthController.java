package com.traveljournal.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.service.AuthService;
import com.traveljournal.global.data.ApiResponse;
import com.traveljournal.global.security.jwt.JwtTokenProvider;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
@Tag(name = "Auth API", description = "로그인 및 로그아웃 관련 API")
public class AuthController {

	private final AuthService authService;
	private final JwtTokenProvider jwtTokenProvider;

	/**
	 * 카카오 로그인 콜백 처리
	 * 카카오 인증 코드를 받아 로그인/회원가입 처리 후 토큰 발급
	 */
	@Operation(
		summary = "Kakao Login callback",
		description = "카카오 인증 코드를 받아 로그인/회원가입 후 헤더에 액세스 토큰을 발급합니다."
	)
	@GetMapping("/kakao/callback")
	public ResponseEntity<LoginResponse> kakaoCallback(
		@Parameter(description = "카카오에서 반환한 인증 코드", required = true)
		@RequestParam String code,
		@Parameter(description = "디바이스 ID (선택 사항)")
		@RequestParam(required = false) String deviceId) {

		LoginCombinedResponse loginCombinedResponse = authService.processKakaoLoginWithCode(code, deviceId);
		return ApiResponse.accessTokenResponse(loginCombinedResponse.LoginResponse(), loginCombinedResponse.accessToken());
	}

	@Operation(
		summary = "Kakao ID Token Login",
		description = "카카오 ID 토큰을 이용한 로그인. Bearer 토큰을 Authorization 헤더에 포함해야 합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/kakao/id-token-login")
	public ResponseEntity<LoginResponse> kakaoLoginWithIdToken(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestParam(required = false) String deviceId
	) {
		String idToken = jwtTokenProvider.resolveToken(authorizationHeader);

		LoginCombinedResponse loginCombinedResponse = authService.processKakaoLoginWithIdToken(idToken, deviceId);

		return ApiResponse.accessTokenResponse(loginCombinedResponse.LoginResponse(), loginCombinedResponse.accessToken());
	}


	/**
	 * 로그아웃 처리
	 * 특정 장치에서 로그아웃 시 해당 장치의 토큰 삭제
	 */
	@Operation(
		summary = "Logout",
		description = "특정 장치에서 로그아웃을 처리하고 해당 장치의 토큰을 삭제합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestParam String deviceId) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		authService.logout(memberId, deviceId);
		return ApiResponse.success("로그아웃되었습니다.");
	}
}