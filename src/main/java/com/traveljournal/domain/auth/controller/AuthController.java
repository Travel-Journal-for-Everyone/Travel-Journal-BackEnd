package com.traveljournal.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "성공"),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청",
			content = @Content(
				mediaType = "text/plain",
				examples = {
					@ExampleObject(name = "지원하지 않는 소셜 로그인 제공자", value = "지원하지 않는 소셜 로그인 제공자입니다 : gogle"),
					@ExampleObject(name = "인증 코드가 비어있거나 null", description = "비어있을때 예시", value = "인증 코드 : ")
				}
			)
		),
		@ApiResponse(
			responseCode = "401",
			description = "인증 정보가 잘못되었거나, 인증이 필요한 상황",
			content = @Content(
				mediaType = "text/plain",
				examples = {
					@ExampleObject(name = "id_token 비어있거나 null", value = "id_token이 비어있거나 null입니다. : idToken"),
					@ExampleObject(name = "id_token 정보 중 sub가 존재하지 않을때", value = "id_token 정보중 회원번호(sub)가 없습니다.")
				}
			)
		),
		@ApiResponse(
			responseCode = "503",
			description = "외부 API 요청 실패",
			content = @Content(
				mediaType = "text/plain",
				examples = {
					@ExampleObject(name = "토큰 발급 실패", description = "유효하지 않은 토큰일 경우 토큰 서버에서 HTTP 400 상태코드와 함께 발급 실패", value = "카카오 토큰 발급에 실패했습니다: 400  on POST request for \"https://kauth.kakao.com/oauth/token\": \"{\"error\":\"invalid_grant\",\"error_description\":\"authorization code not found for code=eee\",\"error_code\":\"KOE320\"}\""),
					@ExampleObject(name = "id_token 파싱 실패", value = "id_token 파싱에 실패했습니다 / 파싱 실패 메시지")
				}
			)
		)
	})
	@GetMapping("/login/{socialProvider}/callback")
	public ResponseEntity<LoginResponse> socialCallback(
		@Parameter(description = "소셜로그인에서 반환한 인증 코드")
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
	public ResponseEntity<LoginResponse> socialLoginWithIdToken(
		@Parameter(description = "소셜에서 반환한 id_Token을 헤더에 담아주세요. Bearer 필요")
		@RequestHeader("Authorization") String authorizationHeader,

		@Parameter(description = "디바이스 ID (선택 사항)")
		@RequestParam(required = false) String deviceId,

		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)")
		@PathVariable String socialProvider,

		@Parameter(description = "플랫폼 (web, ios, android)")
		@RequestHeader(value = "X-Platform", defaultValue = "web") String platform,

		@Parameter(description = "소셜 로그인 Refresh_token, Bearer 필요")
		@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken
	) {
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);

		LoginCombinedResponse loginCombinedResponse = authService.handleLoginWithIdToken(
			socialProviderEnum,
			authorizationHeader,
			deviceId,
			platform,
			refreshToken);

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
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/logout")
	public ResponseEntity<?> logout(
		@Parameter(description = "로그아웃 할 member의 device_id를 입력해주세요.")
		@RequestParam String deviceId) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		authService.logout(memberId, deviceId);
		return ApiResponseHandler.deletedSuccess("로그아웃 성공");
	}

	@Operation(
		summary = "Unlink",
		description = "특정 회원의 연동을 해제합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@DeleteMapping("/{socialProvider}/unlink")
	public ResponseEntity<?> unlinkSocialAccount(
		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)")
		@PathVariable String socialProvider) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);

		authService.unlinkSocialAccount(memberId, socialProviderEnum);
		return ApiResponseHandler.deletedSuccess("연결끊기 성공");
	}
}