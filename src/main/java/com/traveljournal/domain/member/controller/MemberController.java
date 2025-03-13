package com.traveljournal.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.auth.dto.FirstLoginRequest;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.global.data.ResponseHandler;
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
		security = @SecurityRequirement(name = "bearer-key"),
		responses = {
			@ApiResponse(responseCode = "200", ref = "#/components/responses/Success")
		}
	)
	@PostMapping("/complete-first-login")
	public ResponseEntity<?> completeFirstLogin(@RequestBody FirstLoginRequest firstLoginRequest) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		memberService.completeFirstLogin(memberId, firstLoginRequest);
		return ResponseHandler.success("요청이 성공적으로 처리되었습니다.");
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
			return ResponseHandler.onFailure("duplicate");
		}
		else
			return ResponseHandler.success("valid");
	}
}