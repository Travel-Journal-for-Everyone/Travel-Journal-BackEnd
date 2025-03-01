package com.traveljournal.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.auth.dto.FirstLoginRequest;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.global.data.ApiResponse;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
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
		description = "사용자 온보딩이 완료되었을 때 호출됩니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	@PostMapping("/complete-first-login")
	public ResponseEntity<?> completeFirstLogin(@RequestBody FirstLoginRequest firstLoginRequest) {
		Long memberId = SecurityUtil.getCurrentMemberId();

		memberService.completeFirstLogin(memberId, firstLoginRequest);
		return ApiResponse.success("첫 로그인 완료 처리되었습니다.");
	}
}
