package com.traveljournal.domain.auth.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth API", description = "로그인 및 로그아웃 관련 API")
public interface AuthControllerDocs {

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
					@ExampleObject(name = "유효하지 않은 Authorization Header", value = "유효한 Authorization 헤더가 필요합니다. : authorizationHeader"),
					@ExampleObject(name = "인증 코드가 비어있거나 null", value = "인증 코드 : ")
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
					@ExampleObject(name = "토큰 발급 실패", value = "카카오 토큰 발급에 실패했습니다: ..."),
					@ExampleObject(name = "id_token 파싱 실패", value = "id_token 파싱에 실패했습니다 : ...")
				}
			)
		)
	})
	ResponseEntity<?> socialCallback(
		@Parameter(description = "소셜로그인에서 반환한 인증 코드") String code,
		@Parameter(description = "디바이스 ID (선택 사항)") String deviceId,
		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)") String socialProvider,
		@Parameter(description = "플랫폼 (web, ios, android)") String platform
	);

	@Operation(
		summary = "Social ID Token Login",
		description = "ID 토큰을 이용한 로그인. Bearer 토큰을 Authorization 헤더에 포함해야 합니다.<br> X-Platform 헤더에 (web, ios, android)를 포함해야 합니다."
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
					@ExampleObject(name = "유효하지 않은 Authorization Header", value = "유효한 Authorization 헤더가 필요합니다. : authorizationHeader")
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
					@ExampleObject(name = "토큰 발급 실패", value = "카카오 토큰 발급에 실패했습니다: ..."),
					@ExampleObject(name = "id_token 파싱 실패", value = "id_token 파싱에 실패했습니다 : ...")
				}
			)
		)
	})
	ResponseEntity<?> socialLoginWithIdToken(
		@Parameter(description = "소셜에서 반환한 id_Token을 헤더에 담아주세요. Bearer 필요") String authorizationHeader,
		@Parameter(description = "디바이스 ID (선택 사항)") String deviceId,
		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)") String socialProvider,
		@Parameter(description = "플랫폼 (web, ios, android)") String platform,
		@Parameter(description = "소셜 로그인 Refresh_token, Bearer 필요") String refreshToken
	);

	@Operation(
		summary = "Logout",
		description = "특정 장치에서 로그아웃을 처리하고 해당 장치의 토큰을 삭제합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "로그아웃 성공", value = "로그아웃 성공"))
		),
		@ApiResponse(
			responseCode = "401",
			description = "인증 정보가 잘못되었거나, 인증이 필요한 상황",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "올바른 사용자 정보가 아닙니다.", value = "올바른 사용자 정보가 아닙니다."))
		)
	})
	ResponseEntity<?> logout(
		@Parameter(description = "로그아웃 할 member의 device_id를 입력해주세요.") String deviceId
	);

	@Operation(
		summary = "Unlink",
		description = "특정 회원의 연동을 해제합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "성공",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "연결끊기 성공", value = "연결끊기 성공"))
		),
		@ApiResponse(
			responseCode = "400",
			description = "잘못된 요청",
			content = @Content(
				mediaType = "text/plain",
				examples = {
					@ExampleObject(name = "지원하지 않는 소셜 로그인 제공자", value = "지원하지 않는 소셜 로그인 제공자입니다 : gogle"),
					@ExampleObject(name = "카카오 회원번호가 비어 있습니다.", value = "카카오 회원번호가 비어 있습니다.")
				}
			)
		),
		@ApiResponse(
			responseCode = "401",
			description = "인증 정보가 잘못되었거나, 인증이 필요한 상황",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "올바른 사용자 정보가 아닙니다.", value = "올바른 사용자 정보가 아닙니다."))
		),
		@ApiResponse(
			responseCode = "404",
			description = "리소스가 존재하지 않을때",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "회원을 찾을 수 없습니다.", value = "회원을 찾을 수 없습니다. ID: memberId"))
		),
		@ApiResponse(
			responseCode = "503",
			description = "외부 API 요청 실패",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "카카오 연결 끊기 실패", value = "카카오 연결 끊기에 실패했습니다: e.getMessage()"))
		),
		@ApiResponse(
			responseCode = "500",
			description = "서버 내부 오류",
			content = @Content(mediaType = "text/plain", examples = @ExampleObject(name = "회원 삭제 중 오류가 발생했습니다.", value = "회원 삭제 중 오류가 발생했습니다."))
		)
	})
	ResponseEntity<?> unlinkSocialAccount(
		@Parameter(description = "소셜로그인 제공자 (kakao, google, apple)") String socialProvider
	);
}
