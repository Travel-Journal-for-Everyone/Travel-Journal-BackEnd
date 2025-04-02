package com.traveljournal.domain.member.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.dto.ProfileRequest;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/member")
@Tag(name = "member API", description = "회원 관련 API")
public class MemberController {
	private final MemberService memberService;

	/**
	 * 첫 로그인 완료 처리
	 * 사용자 온보딩 완료 시 호출
	 */
	@Operation(
		summary = "Complete First Login",
		description = """
			사용자 온보딩이 완료되었을 때 호출됩니다.
			<br> accountScope = (PUBLIC, FRIENDS, PRIVATE)""",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping(value = "/complete-first-login", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> completeFirstLogin(
		@RequestPart("firstLoginRequest") ProfileRequest profileRequest,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		memberService.updateProfile(memberId, profileRequest, profileImage);
		return ApiResponseHandler.createdSuccess("요청이 성공적으로 처리되었습니다.");
	}

	@GetMapping("/check-nickname/{nickname}")
	@Operation(
		summary = "Nickname Check",
		description = """
			닉네임 중복체크 기능입니다.
			<br> 중복 닉네임 : 상태코드 409 / duplicate
			<br> 비속어 닉네임 : 상태코드 409 / containsBadWord
			<br> 사용가능한 닉네임 : 상태코드 200 / valid""",
		security = @SecurityRequirement(name = "bearer-key"),
		responses = {
			@ApiResponse(responseCode = "200", ref = "#/components/responses/ValidName"),
			@ApiResponse(responseCode = "409", ref = "#/components/responses/DuplicateName")
		}
	)
	public ResponseEntity<?> checkNickname(@PathVariable("nickname") String nickname) {
		// if (memberService.isProfane(nickname)) {
		// 	return ApiResponse.onFailure("containsBadWord");
		// } 비속어 추후 구현 -> mysql 로컬 내부에 있음
		if (memberService.isDuplicate(nickname)) {
			log.info("Check nickname {}", nickname);
			return ApiResponseHandler.onFailure("duplicate");
		} else
			return ApiResponseHandler.onSuccess("valid");
	}

	@GetMapping("/profile")
	@Operation(
		summary = "Member Profile",
		security = @SecurityRequirement(name = "bearer-key"),
		description = "마이페이지에 들어가는 회원 프로필 정보입니다."
	)
	public ResponseEntity<MemberProfileResponse> findMemberProfile() {
		Long memberId = SecurityUtil.getCurrentMemberId();

		MemberProfileResponse memberProfileResponse = memberService.getMemberProfile(memberId);

		return ApiResponseHandler.getObjectSuccess(memberProfileResponse);
	}

	/**
	 * 첫 로그인 완료 처리
	 * 사용자 온보딩 완료 시 호출
	 */
	@Operation(
		summary = "Complete First Login",
		description = """
			사용자 온보딩이 완료되었을 때 호출됩니다.
			<br> accountScope = (PUBLIC, FRIENDS, PRIVATE)""",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PutMapping(value = "/profile/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> updateProfile(
		@RequestPart("profileRequest") ProfileRequest profileRequest,
		@RequestPart(value = "profileImage", required = false) MultipartFile profileImage
	) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		memberService.updateProfile(memberId, profileRequest, profileImage);
		return ApiResponseHandler.createdSuccess("요청이 성공적으로 처리되었습니다.");
	}
}