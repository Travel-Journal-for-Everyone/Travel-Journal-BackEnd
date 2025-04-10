package com.traveljournal.global.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.traveljournal.domain.journal.dto.JournalListResponse;

@Component
public class DummyDataProvider {
	// 실제 DB 연동 없이 더미데이터 반환
	public List<JournalListResponse> getDummyJournalsByRegion(String regionName) {
		List<JournalListResponse> dummyData = new ArrayList<>();
		// 수도권(서울, 경기, 인천) 지역 더미 데이터
		if ("수도권".equals(regionName)) {
			dummyData.add(new JournalListResponse(
				1L,
				Arrays.asList("서울", "여행", "도심"),
				"서울 도심 속 힐링 명소 탐방기",
				2L,
				3L,
				"2025.03.15",
				"2025.03.18"
			));

			dummyData.add(new JournalListResponse(
				2L,
				Arrays.asList("경기도", "수원", "화성"),
				"수원화성 역사 탐방 가족여행",
				1L,
				2L,
				"2025.02.22",
				"2025.02.24"
			));

			dummyData.add(new JournalListResponse(
				3L,
				Arrays.asList("인천", "송도", "주말여행"),
				"송도 센트럴파크 데이트 코스",
				0L,
				1L,
				"2025.04.05",
				"2025.04.05"
			));

			dummyData.add(new JournalListResponse(
				4L,
				Arrays.asList("서울", "맛집", "카페"),
				"서울 핫플레이스 카페 투어",
				1L,
				2L,
				"2025.03.08",
				"2025.03.10"
			));

			dummyData.add(new JournalListResponse(
				5L,
				Arrays.asList("경기도", "가평", "자연"),
				"가평 자연 속 힐링 여행",
				2L,
				3L,
				"2025.01.17",
				"2025.01.20"
			));
		}

		// 강원도 지역 더미 데이터
		else if ("강원도".equals(regionName)) {
			dummyData.add(new JournalListResponse(
				6L,
				Arrays.asList("강원도", "속초", "바다"),
				"속초 바다와 함께한 겨울 여행",
				2L,
				3L,
				"2025.01.10",
				"2025.01.13"
			));

			dummyData.add(new JournalListResponse(
				7L,
				Arrays.asList("강원도", "평창", "스키"),
				"평창 스키장에서의 짜릿한 경험",
				3L,
				4L,
				"2024.12.24",
				"2024.12.28"
			));

			dummyData.add(new JournalListResponse(
				8L,
				Arrays.asList("강원도", "춘천", "닭갈비"),
				"춘천 맛집 투어와 남이섬 산책",
				1L,
				2L,
				"2025.04.01",
				"2025.04.03"
			));

			dummyData.add(new JournalListResponse(
				9L,
				Arrays.asList("강원도", "양양", "서핑"),
				"양양에서 서핑 배우기",
				2L,
				3L,
				"2025.03.20",
				"2025.03.23"
			));

			dummyData.add(new JournalListResponse(
				10L,
				Arrays.asList("강원도", "정선", "힐링"),
				"정선 민둥산 억새풀 여행",
				1L,
				2L,
				"2024.10.15",
				"2024.10.17"
			));
		}

		// 충청도 지역 더미 데이터
		else if ("충청도".equals(regionName)) {
			dummyData.add(new JournalListResponse(
				11L,
				Arrays.asList("충청도", "대전", "과학관"),
				"대전 국립중앙과학관 탐방기",
				1L,
				2L,
				"2025.02.08",
				"2025.02.10"
			));

			dummyData.add(new JournalListResponse(
				12L,
				Arrays.asList("충청도", "공주", "역사"),
				"공주 백제문화제와 역사 여행",
				2L,
				3L,
				"2024.09.25",
				"2024.09.28"
			));

			dummyData.add(new JournalListResponse(
				13L,
				Arrays.asList("충청도", "천안", "독립기념관"),
				"천안 독립기념관 역사 탐방",
				0L,
				1L,
				"2025.03.01",
				"2025.03.01"
			));

			dummyData.add(new JournalListResponse(
				14L,
				Arrays.asList("충청도", "보령", "머드축제"),
				"보령 머드축제 즐기기",
				3L,
				4L,
				"2024.07.15",
				"2024.07.19"
			));

			dummyData.add(new JournalListResponse(
				15L,
				Arrays.asList("충청도", "단양", "도담삼봉"),
				"단양 8경 투어와 패러글라이딩",
				2L,
				3L,
				"2025.04.05",
				"2025.04.08"
			));
		}

		// 경상도 지역 더미 데이터
		else if ("경상도".equals(regionName)) {
			dummyData.add(new JournalListResponse(
				16L,
				Arrays.asList("경상도", "부산", "해운대"),
				"바다가 주구장창 보고싶던 부산 여행",
				2L,
				3L,
				"2025.02.05",
				"2025.02.08"
			));

			dummyData.add(new JournalListResponse(
				17L,
				Arrays.asList("경상도", "경주", "불국사"),
				"남자 혼자 떠나는 가을 경주 여행",
				2L,
				3L,
				"2024.10.15",
				"2024.10.18"
			));

			dummyData.add(new JournalListResponse(
				18L,
				Arrays.asList("경상도", "안동", "가족여행"),
				"가을 느낌 한가득! 울긋불긋 안동",
				1L,
				2L,
				"2024.11.01",
				"2024.11.03"
			));

			dummyData.add(new JournalListResponse(
				19L,
				Arrays.asList("경상도", "맛집"),
				"맛집 찾기는 언제 멈춰야하나?",
				3L,
				4L,
				"2025.01.24",
				"2025.01.28"
			));

			dummyData.add(new JournalListResponse(
				20L,
				Arrays.asList("경상도", "거제도", "힐링여행"),
				"바다에서 가을 물결 여행",
				2L,
				3L,
				"2024.10.01",
				"2024.10.12"
			));
		}

		// 전라도 지역 더미 데이터
		else if ("전라도".equals(regionName)) {
			dummyData.add(new JournalListResponse(
				21L,
				Arrays.asList("전라도", "광주", "맛집"),
				"광주 맛집 투어와 문화 체험",
				2L,
				3L,
				"2025.03.10",
				"2025.03.13"
			));

			dummyData.add(new JournalListResponse(
				22L,
				Arrays.asList("전라도", "전주", "한옥마을"),
				"전주 한옥마을 한복 체험",
				1L,
				2L,
				"2025.02.15",
				"2025.02.17"
			));

			dummyData.add(new JournalListResponse(
				23L,
				Arrays.asList("전라도", "여수", "밤바다"),
				"여수 밤바다 로맨틱 여행",
				2L,
				3L,
				"2025.01.20",
				"2025.01.23"
			));

			dummyData.add(new JournalListResponse(
				24L,
				Arrays.asList("전라도", "순천", "순천만"),
				"순천만 습지와 순천만국가정원",
				1L,
				2L,
				"2025.04.08",
				"2025.04.10"
			));

			dummyData.add(new JournalListResponse(
				25L,
				Arrays.asList("전라도", "담양", "죽녹원"),
				"담양 죽녹원과 메타세쿼이아길",
				0L,
				1L,
				"2024.11.09",
				"2024.11.09"
			));
		}

		// 제주도 지역 더미 데이터
		else if ("제주도".equals(regionName)) {
			dummyData.add(new JournalListResponse(
				26L,
				Arrays.asList("제주도", "성산일출봉", "우도"),
				"제주도 동쪽 여행 코스",
				3L,
				4L,
				"2025.03.25",
				"2025.03.29"
			));

			dummyData.add(new JournalListResponse(
				27L,
				Arrays.asList("제주도", "한라산", "등산"),
				"한라산 등반과 오름 투어",
				4L,
				5L,
				"2024.10.10",
				"2024.10.15"
			));

			dummyData.add(new JournalListResponse(
				28L,
				Arrays.asList("제주도", "서귀포", "카페"),
				"서귀포 해안도로 카페 투어",
				2L,
				3L,
				"2025.02.20",
				"2025.02.23"
			));

			dummyData.add(new JournalListResponse(
				29L,
				Arrays.asList("제주도", "맛집", "흑돼지"),
				"제주 맛집 탐방과 흑돼지 먹방",
				3L,
				4L,
				"2025.01.05",
				"2025.01.09"
			));

			dummyData.add(new JournalListResponse(
				30L,
				Arrays.asList("제주도", "올레길", "힐링"),
				"제주 올레길 걷기 여행",
				5L,
				6L,
				"2025.04.01",
				"2025.04.07"
			));
		}

		return dummyData;
	}
}
