package com.traveljournal.domain.memberDashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.memberDashboard.dto.MemberDashbordResponse;
import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.memberDashboard.dto.RegionInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDashboardService {

	private final MemberService memberService;

	public MemberDashbordResponse getMemberDashbord(Long memberId) {

		MemberProfileResponse memberProfileResponse = memberService.getMemberProfile(memberId);

		List<RegionInfo> regionInfos = getRegionInfoList();

		return new MemberDashbordResponse(memberProfileResponse, regionInfos);
	}

	public List<RegionInfo> getRegionInfoList() {

		return List.of(
			new RegionInfo("수도권", 57L, 88L),
			new RegionInfo("강원도", 377L, 444L),
			new RegionInfo("충청도", 21L, 5L),
			new RegionInfo("경상도", 97L, 70L),
			new RegionInfo("전라도", 157L, 665L),
			new RegionInfo("제주도", 999L, 1005L)
		);
	}
}
