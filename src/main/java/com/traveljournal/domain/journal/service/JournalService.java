package com.traveljournal.domain.journal.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.service.ImageInfoService;
import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.hashtag.service.HashTagService;
import com.traveljournal.domain.journal.dto.JournalCreateRequest;
import com.traveljournal.domain.journal.dto.JournalDayRequest;
import com.traveljournal.domain.journal.dto.JournalDaySpotRequest;
import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.entity.JournalDay;
import com.traveljournal.domain.journal.entity.JournalDaySpot;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.photo.dto.PhotoMetadataRequest;
import com.traveljournal.domain.photo.entity.Photo;
import com.traveljournal.domain.photo.service.PhotoService;
import com.traveljournal.domain.statistics.service.MemberRegionStatisticsService;
import com.traveljournal.domain.statistics.service.MemberStatisticsService;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.util.RegionGroupUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JournalService {

	private final JournalRepository journalRepository;
	private final HashTagService hashTagService;
	private final ImageInfoService imageInfoService;
	private final MemberRegionStatisticsService memberRegionStatisticsService;
	private final ImageService imageService;
	private final MemberService memberService;
	private final BlockService blockService;
	private final MemberStatisticsService memberStatisticsService;
	private final PhotoService photoService;

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findJournalsByRegionWithPaging(Long memberId, Long viewerId, String regionName,
		Pageable pageable) {
		blockService.validateNotBlocked(viewerId, memberId);

		List<String> regionList = RegionGroupUtil.getRegionList(regionName);
		List<Long> blockedIds = blockService.getBlockedMemberIds(viewerId);

		Page<Long> journalIdPage = journalRepository.findIdsByMemberIdAndRegionInExcludingBlocked(memberId, regionList, blockedIds, pageable);
		return getJournalListResponses(pageable, journalIdPage);
	}

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findAllJournalsByMemberId(Long memberId, Long viewerId, Pageable pageable) {
		blockService.validateNotBlocked(viewerId, memberId);

		List<Long> blockedIds = blockService.getBlockedMemberIds(viewerId);
		Page<Long> journalIdPage = journalRepository.findIdsByMemberIdExcludingBlocked(memberId, blockedIds, pageable);
		return getJournalListResponses(pageable, journalIdPage);
	}

	private Page<JournalListResponse> getJournalListResponses(Pageable pageable, Page<Long> journalIdPage) {
		List<Long> journalIds = journalIdPage.getContent();

		List<Journal> journals = journalRepository.findAllByIdInFetchJoin(journalIds);

		Map<Long, Journal> journalMap = journals.stream().collect(Collectors.toMap(Journal::getId, j -> j));
		List<Journal> sortedJournals = journalIds.stream().map(journalMap::get).toList();

		return new PageImpl<>(
			sortedJournals.stream()
				.map(journal -> new JournalListResponse(
					journal.getId(),
					journal.getHashTags().stream().map(HashTag::getTagName).toList(),
					journal.getTitle(),
					journal.getNights(),
					journal.getDays(),
					journal.getStartDate(),
					journal.getEndDate()
				))
				.toList(),
			pageable,
			journalIdPage.getTotalElements()
		);
	}
	@Transactional
	public Long createJournal(JournalCreateRequest request, Long memberId) {
		validateRequest(request);

		Member member = memberService.findById(memberId);
		List<HashTag> tags = hashTagService.getOrCreateHashTags(request.hashTag());

		Journal journal = createJournalEntity(request, member, tags);

		List<JournalDay> journalDays = createJournalDays(request.journalDays(), journal);

		addPhotosToDays(journalDays, request.photoMetadataList());

		setThumbnailUrl(journal, journalDays);

		journalRepository.save(journal);

		memberStatisticsService.increaseTravelDiaryCount(memberId);

		memberRegionStatisticsService.increaseTravelDiaryCount(memberId, journal.getRegion());

		return journal.getId();
	}


	private void validateRequest(JournalCreateRequest request) {
		if (request.title() == null || request.title().isBlank()) {
			throw new BadRequestException("여행일지 제목은 필수입니다.");
		}
		if (request.journalDays() == null || request.journalDays().isEmpty()) {
			throw new BadRequestException("여행일지의 일차 정보가 필요합니다.");
		}
		if (request.photoMetadataList() == null) {
			throw new BadRequestException("사진 메타데이터가 필요합니다.");
		}
	}

	private Journal createJournalEntity(JournalCreateRequest request, Member member, List<HashTag> tags) {
		return Journal.builder()
			.title(request.title())
			.region(request.region())
			.nights(request.nights())
			.days(request.days())
			.startDate(request.startDate())
			.endDate(request.endDate())
			.member(member)
			.hashTags(tags)
			.createdAt(LocalDateTime.now())
			.build();
	}

	private List<JournalDay> createJournalDays(List<JournalDayRequest> dayRequests, Journal journal) {
		List<JournalDay> journalDays = new ArrayList<>();
		for (JournalDayRequest dayReq : dayRequests) {
			JournalDay day = JournalDay.builder()
				.dayNumber(dayReq.dayNumber())
				.description(dayReq.description())
				.journal(journal)
				.build();
			for (JournalDaySpotRequest spotReq : dayReq.journalDaySpots()) {
				JournalDaySpot spot = JournalDaySpot.builder()
					.spotOrder(spotReq.spotOrder())
					.spotName(spotReq.spotName())
					.latitude(spotReq.latitude())
					.longitude(spotReq.longitude())
					.journalDay(day)
					.build();
				day.getSpots().add(spot);
			}
			journal.addDay(day);
			journalDays.add(day);
		}
		return journalDays;
	}

	private void addPhotosToDays(List<JournalDay> journalDays, List<PhotoMetadataRequest> photoMetas) {
		Set<String> uniqueUploadIds = new HashSet<>();
		for (JournalDay day : journalDays) {
			int dayNum = day.getDayNumber();
			for (PhotoMetadataRequest meta : photoMetas) {
				if (meta.dayNumber() != dayNum)
					continue;
				if (!uniqueUploadIds.add(meta.uploadId()))
					continue;

				ImageInfo imageInfo = imageInfoService.getImageInfo(meta.uploadId());

				photoService.existsByImageInfo(imageInfo, meta.uploadId());

				Photo photo = Photo.builder()
					.description(meta.description())
					.placeName(meta.address())
					.takenDateTime(
						LocalDateTime.parse(meta.takenDateTime(), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")))
					.latitude(meta.latitude())
					.longitude(meta.longitude())
					.imageInfo(imageInfo)
					.build();

				day.addPhoto(photo);
			}
		}
	}

	private void setThumbnailUrl(Journal journal, List<JournalDay> journalDays) {
		String thumbnailUrl = null;
		if (!journalDays.isEmpty() && !journalDays.get(0).getPhotos().isEmpty()) {
			Photo firstPhoto = journalDays.get(0).getPhotos().get(0);
			thumbnailUrl = imageService.getImageUrl(firstPhoto.getImageInfo().getFilename());
		}
		journal.setThumbnailUrl(thumbnailUrl);
	}
}