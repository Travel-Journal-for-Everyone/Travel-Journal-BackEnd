package com.traveljournal.domain.memberDashboard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traveljournal.domain.memberDashboard.dto.MemberDashbordResponse;
import com.traveljournal.domain.memberDashboard.service.MemberDashboardService;
import com.traveljournal.global.data.ApiResponseHandler;
import com.traveljournal.global.security.util.SecurityUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "Member Dashboard API", description = "사용자 대시보드 API")
public class MemberDashboardController {
	private final MemberDashboardService memberDashboardService;

	@GetMapping("/{memberId}")
	@Operation(
		security = @SecurityRequirement(name = "bearer-key")
	)
	public ResponseEntity<MemberDashbordResponse> getMemberDashborad(
		@PathVariable("memberId") Long memberId) {
		Long currentMemberId = SecurityUtil.getCurrentMemberId();

		MemberDashbordResponse memberDashbordResponse = memberDashboardService.getMemberDashbord(memberId, currentMemberId);

		return ApiResponseHandler.getObjectSuccess(memberDashbordResponse);
	}
}
