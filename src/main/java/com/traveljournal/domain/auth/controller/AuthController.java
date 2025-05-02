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

import com.traveljournal.domain.auth.controller.docs.AuthControllerDocs;
import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.service.AuthService;
import com.traveljournal.domain.auth.util.EnumUtils;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController implements AuthControllerDocs {

	private final AuthService authService;

	@GetMapping("/login/{socialProvider}/callback")
	@Override
	public ResponseEntity<?> socialCallback(
		@RequestParam String code,
		@RequestParam(required = false) String deviceId,
		@PathVariable String socialProvider,
		@RequestHeader(value = "X-Platform", defaultValue = "web") String platform
	) {
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);
		LoginCombinedResponse loginCombinedResponse = authService.handleLoginWithCode(
			socialProviderEnum, code, deviceId, platform
		);
		return ApiResponseHandler.accessTokenResponse(
			loginCombinedResponse.LoginResponse(), loginCombinedResponse.accessToken()
		);
	}

	@PostMapping("/login/{socialProvider}/id-token")
	@Override
	public ResponseEntity<?> socialLoginWithIdToken(
		@RequestHeader("Authorization") String authorizationHeader,
		@RequestParam(required = false) String deviceId,
		@PathVariable String socialProvider,
		@RequestHeader(value = "X-Platform", defaultValue = "web") String platform,
		@RequestHeader(value = "X-Refresh-Token", required = false) String refreshToken
	) {
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);
		LoginCombinedResponse loginCombinedResponse = authService.handleLoginWithIdToken(
			socialProviderEnum, authorizationHeader, deviceId, platform, refreshToken
		);
		return ApiResponseHandler.accessTokenResponse(
			loginCombinedResponse.LoginResponse(), loginCombinedResponse.accessToken()
		);
	}

	@PostMapping("/logout")
	@Override
	public ResponseEntity<?> logout(@RequestParam String deviceId) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		authService.logout(memberId, deviceId);
		return ApiResponseHandler.deletedSuccess("로그아웃 성공");
	}

	@DeleteMapping("/{socialProvider}/unlink")
	@Override
	public ResponseEntity<?> unlinkSocialAccount(@PathVariable String socialProvider) {
		Long memberId = SecurityUtil.getCurrentMemberId();
		SocialProvider socialProviderEnum = EnumUtils.toSocialProvider(socialProvider);
		authService.unlinkSocialAccount(memberId, socialProviderEnum);
		return ApiResponseHandler.deletedSuccess("연결끊기 성공");
	}
}