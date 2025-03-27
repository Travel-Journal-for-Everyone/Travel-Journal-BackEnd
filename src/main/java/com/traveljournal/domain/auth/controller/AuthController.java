package com.traveljournal.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.service.AuthService;
import com.traveljournal.domain.auth.util.EnumUtils;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

	/**
	 * 로그인 콜백 처리
	 * 인증 코드를 받아 로그인/회원가입 처리 후 토큰 발급
	 */
	@Operation(
		summary = "Social Login callback",
		description = "인증 코드를 받아 로그인/회원가입 후 헤더에 액세스 토큰을 발급합니다."
	)
	@GetMapping("/login/{socialProvider}/callback")
	public ResponseEntity<LoginResponse> kakaoCallback(
		@Parameter(description = "카카오에서 반환한 인증 코드")
		@RequestParam String code,

		@Parameter(description = "디바이스 ID (선택 사항)")
		@RequestParam(required = false) String deviceId,

		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)")
		@PathVariable String socialProvider,

		@Parameter(description = "플랫폼 (web, ios, android)")
		@RequestHeader(value = "X-Platform", defaultValue = "web") String platform
	) {
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);

		LoginCombinedResponse loginCombinedResponse = authService.handleLoginWithCode(socialProviderEnum, code,
			deviceId, platform);

		return ApiResponseHandler.accessTokenResponse(loginCombinedResponse.LoginResponse(),
			loginCombinedResponse.accessToken());
	}

	@Operation(
		summary = "Social ID Token Login",
		description = """
			ID 토큰을 이용한 로그인. Bearer 토큰을 Authorization 헤더에 포함해야 합니다.
			<br> X-Platform 헤더에 (web, ios, android)를 포함해야 합니다."""
	)
	@PostMapping("/login/{socialProvider}/id-token")
	public ResponseEntity<LoginResponse> kakaoLoginWithIdToken(
		@Parameter(description = "카카오에서 반환한 id_Token을 헤더에 담아주세요. Bearer 필요")
		@RequestHeader("Authorization") String authorizationHeader,

		@Parameter(description = "디바이스 ID (선택 사항)")
		@RequestParam(required = false) String deviceId,

		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)")
		@PathVariable String socialProvider,

		@Parameter(description = "플랫폼 (web, ios, android)")
		@RequestHeader(value = "X-Platform", defaultValue = "web") String platform
	) {
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);

		LoginCombinedResponse loginCombinedResponse = authService.handleLoginWithIdToken(socialProviderEnum,
			authorizationHeader, deviceId, platform);

		return ApiResponseHandler.accessTokenResponse(loginCombinedResponse.LoginResponse(),
			loginCombinedResponse.accessToken());
	}

	/**
	 * 로그아웃 처리
	 * 특정 장치에서 로그아웃 시 해당 장치의 토큰 삭제
	 */
	@Operation(
		summary = "Logout",
		description = "특정 장치에서 로그아웃을 처리하고 해당 장치의 토큰을 삭제합니다.",
		security = @SecurityRequirement(name = "bearer-key"),
		responses = {
			@ApiResponse(responseCode = "200", ref = "#/components/responses/Logout"),
		}
	)
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
		@Parameter(description = "로그아웃 할 member의 device_id를 입력해주세요.")
		@RequestParam String deviceId) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		authService.logout(memberId, deviceId);
		return ApiResponseHandler.deletedSuccess("로그아웃 성공");
	}
}