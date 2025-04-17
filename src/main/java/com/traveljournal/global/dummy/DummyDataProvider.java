package com.traveljournal.global.dummy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.place.dto.PlaceListResponse;

@Component
public class DummyDataProvider {
	// 실제 DB 연동 없이 더미데이터 반환
	public List<JournalListResponse> getDummyJournalsByRegion(String regionName) {
		List<JournalListResponse> dummyData = new ArrayList<>();
		if ("all".equalsIgnoreCase(regionName)) {
			// 모든 지역 더미 데이터 합치기
			dummyData.addAll(getDummyJournalsByRegion("수도권"));
			dummyData.addAll(getDummyJournalsByRegion("강원도"));
			dummyData.addAll(getDummyJournalsByRegion("충청도"));
			dummyData.addAll(getDummyJournalsByRegion("전라도"));
			dummyData.addAll(getDummyJournalsByRegion("제주도"));
		}
		// 수도권(서울, 경기, 인천) 지역 더미 데이터
		else {
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

				dummyData.add(new JournalListResponse(
					31L,
					Arrays.asList("제주도", "중문", "해변"),
					"중문 해변에서의 여름 휴가",
					3L,
					4L,
					"2025.07.15",
					"2025.07.19"
				));

				dummyData.add(new JournalListResponse(
					32L,
					Arrays.asList("제주도", "애월", "카페"),
					"애월 해안도로 카페 투어",
					2L,
					3L,
					"2025.05.10",
					"2025.05.13"
				));

				dummyData.add(new JournalListResponse(
					33L,
					Arrays.asList("제주도", "협재", "해수욕장"),
					"에메랄드빛 협재해수욕장 여행",
					2L,
					3L,
					"2025.08.01",
					"2025.08.04"
				));

				dummyData.add(new JournalListResponse(
					34L,
					Arrays.asList("제주도", "한림", "수목원"),
					"한림공원과 협재해변 당일치기",
					0L,
					1L,
					"2025.06.15",
					"2025.06.15"
				));

				dummyData.add(new JournalListResponse(
					35L,
					Arrays.asList("제주도", "비자림", "산책"),
					"비자림 숲길 산책과 힐링",
					1L,
					2L,
					"2025.09.20",
					"2025.09.22"
				));

				dummyData.add(new JournalListResponse(
					36L,
					Arrays.asList("제주도", "만장굴", "탐험"),
					"만장굴 탐험과 김녕 해변",
					1L,
					2L,
					"2025.04.15",
					"2025.04.17"
				));

				dummyData.add(new JournalListResponse(
					37L,
					Arrays.asList("제주도", "마라도", "최남단"),
					"대한민국 최남단 마라도 여행",
					2L,
					3L,
					"2025.05.25",
					"2025.05.28"
				));

				dummyData.add(new JournalListResponse(
					38L,
					Arrays.asList("제주도", "천지연폭포", "야경"),
					"천지연폭포 야경과 서귀포 맛집",
					2L,
					3L,
					"2025.02.10",
					"2025.02.13"
				));

				dummyData.add(new JournalListResponse(
					39L,
					Arrays.asList("제주도", "섭지코지", "드라마"),
					"드라마 촬영지 섭지코지 투어",
					1L,
					2L,
					"2025.03.05",
					"2025.03.07"
				));

				dummyData.add(new JournalListResponse(
					40L,
					Arrays.asList("제주도", "카멜리아힐", "꽃"),
					"카멜리아힐 동백꽃 구경",
					1L,
					2L,
					"2025.01.15",
					"2025.01.17"
				));

				dummyData.add(new JournalListResponse(
					41L,
					Arrays.asList("제주도", "사려니숲길", "트레킹"),
					"사려니숲길 트레킹과 치유",
					2L,
					3L,
					"2025.06.05",
					"2025.06.08"
				));

				dummyData.add(new JournalListResponse(
					42L,
					Arrays.asList("제주도", "쇠소깍", "카약"),
					"쇠소깍 카약 체험과 주상절리",
					1L,
					2L,
					"2025.07.25",
					"2025.07.27"
				));

				dummyData.add(new JournalListResponse(
					43L,
					Arrays.asList("제주도", "오설록", "녹차"),
					"오설록 녹차밭과 이니스프리",
					1L,
					2L,
					"2025.04.20",
					"2025.04.22"
				));

				dummyData.add(new JournalListResponse(
					44L,
					Arrays.asList("제주도", "아쿠아플라넷", "수족관"),
					"아쿠아플라넷 제주 가족 나들이",
					2L,
					3L,
					"2025.08.15",
					"2025.08.18"
				));

				dummyData.add(new JournalListResponse(
					45L,
					Arrays.asList("제주도", "용두암", "일출"),
					"용두암에서 본 제주 일출",
					1L,
					2L,
					"2025.01.01",
					"2025.01.03"
				));

				dummyData.add(new JournalListResponse(
					46L,
					Arrays.asList("제주도", "우도", "자전거"),
					"우도 자전거 일주 여행",
					1L,
					2L,
					"2025.05.05",
					"2025.05.07"
				));

				dummyData.add(new JournalListResponse(
					47L,
					Arrays.asList("제주도", "함덕", "해수욕장"),
					"함덕 해수욕장 서핑 체험",
					2L,
					3L,
					"2025.07.05",
					"2025.07.08"
				));

				dummyData.add(new JournalListResponse(
					48L,
					Arrays.asList("제주도", "산방산", "산책"),
					"산방산과 용머리해안 트레킹",
					1L,
					2L,
					"2025.09.10",
					"2025.09.12"
				));
			}
		}

		return dummyData;
	}

	public List<PlaceListResponse> getDummyPlacesByRegion(String regionName) {
		List<PlaceListResponse> dummyData = new ArrayList<>();
		String imageUrl = "https://search.pstatic.net/common/?src=http%3A%2F%2Fblogfiles.naver.net%2FMjAyNTAzMjFfMTEw%2FMDAxNzQyNTU5MjY0OTkz.US8DxCfatYon23fMlPjPlqGIvpK8Zd8SIP3BuNFyGmUg.LUZS2bZBQ1aJuHyVI52EjhzHykDFewCj4mpJCeoV0G0g.JPEG%2F2025%25BA%25A2%25B2%25C9%25B0%25B3%25C8%25AD%25BD%25C3%25B1%25E2IMG_2503-009.JPG&type=sc960_832";

		if("all".equalsIgnoreCase(regionName)) {
			dummyData.addAll(getDummyPlacesByRegion("수도권"));
			dummyData.addAll(getDummyPlacesByRegion("강원도"));
			dummyData.addAll(getDummyPlacesByRegion("충청도"));
			dummyData.addAll(getDummyPlacesByRegion("전라도"));
			dummyData.addAll(getDummyPlacesByRegion("제주도"));
		}
		// 수도권(서울, 경기, 인천) 지역 더미 데이터
		else {
			if ("수도권".equals(regionName)) {
			dummyData.add(new PlaceListResponse(
				1L,
				"서울 타워",
				"서울",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				2L,
				"경복궁",
				"서울",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				3L,
				"인천 차이나타운",
				"인천",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				4L,
				"수원 화성",
				"경기도",
				imageUrl
			));
		}

		// 강원도 지역 더미 데이터
		else if ("강원도".equals(regionName)) {
			dummyData.add(new PlaceListResponse(
				5L,
				"속초 해변",
				"강원도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				6L,
				"평창 스키장",
				"강원도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				7L,
				"춘천 남이섬",
				"강원도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				8L,
				"양양 서핑 스팟",
				"강원도",
				imageUrl
			));
		}

		// 충청도 지역 더미 데이터
		else if ("충청도".equals(regionName)) {
			dummyData.add(new PlaceListResponse(
				9L,
				"대전 과학관",
				"충청도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				10L,
				"공주 백제문화제",
				"충청도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				11L,
				"천안 독립기념관",
				"충청도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				12L,
				"보령 머드축제",
				"충청도",
				imageUrl
			));
		}

		// 전라도 지역 더미 데이터
		else if ("전라도".equals(regionName)) {
			dummyData.add(new PlaceListResponse(
				17L,
				"전주 한옥마을",
				"전라도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				18L,
				"여수 밤바다",
				"전라도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				19L,
				"순천만 습지",
				"전라도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				20L,
				"담양 죽녹원",
				"전라도",
				imageUrl
			));
		}

		// 제주도 지역 더미 데이터 (기존 10개 + 7개 추가)
		else if ("제주도".equals(regionName)) {
			dummyData.add(new PlaceListResponse(
				21L,
				"성산일출봉",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				22L,
				"한라산",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				23L,
				"협재 해수욕장",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				24L,
				"우도",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				25L,
				"만장굴",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				26L,
				"천지연폭포",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				27L,
				"오설록 티 뮤지엄",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				28L,
				"카멜리아힐",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				29L,
				"사려니숲길",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				30L,
				"섭지코지",
				"제주도",
				imageUrl
			));

			// 추가 7개 장소
			dummyData.add(new PlaceListResponse(
				31L,
				"정모시쉼터",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				32L,
				"교래자연휴양림",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				33L,
				"비자림",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				34L,
				"용두암",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				35L,
				"주상절리대",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				36L,
				"제주민속촌",
				"제주도",
				imageUrl
			));

			dummyData.add(new PlaceListResponse(
				37L,
				"에코랜드",
				"제주도",
				imageUrl
			));
		}
	}
		return dummyData;
	}
}
