package com.traveljournal.domain.memberDashboard.dto;

import java.util.List;

import com.traveljournal.domain.member.dto.MemberProfileResponse;

public record MemberDashbordResponse(
	MemberProfileResponse profileInfo,
	List<RegionInfo> regions
) {}
