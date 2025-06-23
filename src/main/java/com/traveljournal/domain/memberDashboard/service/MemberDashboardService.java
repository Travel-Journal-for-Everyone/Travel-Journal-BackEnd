package com.traveljournal.domain.memberDashboard.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.block.dto.BlockRelationType;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.memberDashboard.dto.MemberDashbordResponse;
import com.traveljournal.domain.memberDashboard.dto.RegionInfo;
import com.traveljournal.domain.place.repository.PlaceRepository;
import com.traveljournal.domain.statistics.entity.MemberRegionStatistics;
import com.traveljournal.domain.statistics.entity.MemberStatistics;
import com.traveljournal.domain.statistics.repository.MemberRegionStatisticsRepository;
import com.traveljournal.global.util.RegionGroupUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDashboardService {

	private final MemberService memberService;
	private final BlockService blockService;
	private final PlaceRepository placeRepository;
	private final JournalRepository journalRepository;
	private final MemberRegionStatisticsRepository memberRegionStatisticsRepository;

	public MemberDashbordResponse getMemberDashbord(Long memberId, Long viewerId) {

		Member member = memberService.findById(memberId);

		MemberStatistics memberStatistics = memberService.getMemberStatistics(memberId);

		BlockRelationType blockRelationType = blockService.getBlockRelation(viewerId, memberId);

		MemberProfileResponse memberProfileResponse = MemberProfileResponse.of(member, memberStatistics, blockRelationType);

		List<RegionInfo> regionInfos = getRegionStatistics(memberId);

		return new MemberDashbordResponse(memberProfileResponse, regionInfos);
	}

	public List<RegionInfo> getRegionStatistics (Long memberId) {

		List<MemberRegionStatistics> statsList = memberRegionStatisticsRepository.findAllByIdMemberId(memberId);

		Map<String, MemberRegionStatistics> statsMap = statsList.stream()
			.collect(Collectors.toMap(
				s -> s.getId().getRegionGroup(),
				s -> s
			));

		return RegionGroupUtil.REGION_GROUP_MAP.keySet().stream()
			.map(group -> {
				List<String> regionList = RegionGroupUtil.getRegionList(group);
				long travelDiaryCount = regionList.stream()
					.map(statsMap::get)
					.filter(Objects::nonNull)
					.mapToLong(MemberRegionStatistics::getTravelDiaryCount)
					.sum();
				long placesCount = regionList.stream()
					.map(statsMap::get)
					.filter(Objects::nonNull)
					.mapToLong(MemberRegionStatistics::getPlacesCount)
					.sum();
				return RegionInfo.of(group, travelDiaryCount, placesCount);
			})
			.toList();
	}

	//직접 계산 - 추후 필요할 수 있기에 남겨둡니다.
	public List<RegionInfo> getRegionInfoList(Long memberId) {
		List<Object[]> placeRegionCounts = placeRepository.countPlacesByRegion(memberId);
		List<Object[]> journalRegionCounts = journalRepository.countJournalsByRegion(memberId);

		Map<String, Long> placeRegionCountMap = placeRegionCounts.stream()
			.collect(Collectors.toMap(
				obj -> (String) obj[0],
				obj -> (Long) obj[1]
			));

		Map<String, Long> journalRegionCountMap = journalRegionCounts.stream()
			.collect(Collectors.toMap(
				obj -> (String) obj[0],
				obj -> (Long) obj[1]
			));

		return RegionGroupUtil.REGION_GROUP_MAP.keySet().stream()
			.map(group -> {
				List<String> regionList = RegionGroupUtil.getRegionList(group);
				long placeCount = sumRegionCount(regionList, placeRegionCountMap);
				long journalCount = sumRegionCount(regionList, journalRegionCountMap);
				return RegionInfo.of(group, journalCount, placeCount);
			})
			.toList();
	}

	private long sumRegionCount(List<String> regionList, Map<String, Long> countMap) {
		return regionList.stream()
			.mapToLong(region -> countMap.getOrDefault(region, 0L))
			.sum();
	}
}
