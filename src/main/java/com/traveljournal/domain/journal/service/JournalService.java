package com.traveljournal.domain.journal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.block.dto.BlockRelationType;
import com.traveljournal.domain.block.service.BlockService;
import com.traveljournal.domain.explore.repository.ExploreSeenJournalRepository;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.hashtag.service.HashTagService;
import com.traveljournal.domain.journal.dto.JournalCreateRequest;
import com.traveljournal.domain.journal.dto.JournalDayRequest;
import com.traveljournal.domain.journal.dto.JournalDaySpotRequest;
import com.traveljournal.domain.journal.dto.JournalDetailResponse;
import com.traveljournal.domain.journal.dto.JournalListResponse;
import com.traveljournal.domain.journal.dto.JournalUpdateRequest;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.entity.JournalDay;
import com.traveljournal.domain.journal.entity.JournalDaySpot;
import com.traveljournal.domain.journal.repository.JournalRepository;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.photo.dto.PhotoListResponse;
import com.traveljournal.domain.photo.dto.PhotoMetadataRequest;
import com.traveljournal.domain.photo.entity.Photo;
import com.traveljournal.domain.photo.service.PhotoService;
import com.traveljournal.domain.statistics.service.MemberRegionStatisticsService;
import com.traveljournal.domain.statistics.service.MemberStatisticsService;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.JournalNotFoundException;
import com.traveljournal.global.util.RegionGroupUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

	private final JournalRepository journalRepository;
	private final ExploreSeenJournalRepository exploreSeenJournalRepository;
	private final HashTagService hashTagService;
	private final MemberRegionStatisticsService memberRegionStatisticsService;
	private final ImageService imageService;
	private final MemberService memberService;
	private final BlockService blockService;
	private final MemberStatisticsService memberStatisticsService;
	private final PhotoService photoService;

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findJournalsByRegionWithPaging(Long memberId, Long viewerId, String regionName,
		Pageable pageable) {
		validateAccess(viewerId, memberId);

		List<String> regionList = RegionGroupUtil.getRegionList(regionName);
		List<Long> blockedIds = blockService.getBlockedMemberIds(viewerId);

		Page<Long> journalIdPage = journalRepository.findIdsByMemberIdAndRegionInExcludingBlocked(memberId, regionList,
			blockedIds, pageable);
		return getJournalListResponses(pageable, journalIdPage);
	}

	@Transactional(readOnly = true)
	public Page<JournalListResponse> findAllJournalsByMemberId(Long memberId, Long viewerId, Pageable pageable) {
		validateAccess(viewerId, memberId);

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
					journal.getEndDate(),
					journal.getThumbnailUrl(imageService)
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

		journal.updateDaysDetail(journalDays);

		photoService.processJournalPhotos(journalDays, request.photoMetadataList());

		photoService.setJournalThumbnail(journal, journalDays, request.thumbnailUploadId());

		journalRepository.save(journal);

		updateStatisticsForCreate(memberId, journal.getRegion());

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
			.description(request.description())
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

	@Transactional(readOnly = true)
	public JournalDetailResponse getJournalDetail(Long journalId, Long currentMemberId) {
		Journal journal = journalRepository.findById(journalId)
			.orElseThrow(() -> new JournalNotFoundException("해당하는 여행일지가 없습니다."));

		BlockRelationType blockRelationType = blockService.getBlockRelation(currentMemberId,
			journal.getMember().getId());
		return JournalDetailResponse.of(journal, blockRelationType, imageService);
	}

	@Transactional(readOnly = true)
	public List<PhotoListResponse> getJournalPhotos(Long journalId, Long viewerId) {
		Journal journal = journalRepository.findById(journalId)
			.orElseThrow(() -> new JournalNotFoundException("해당하는 여행일지가 없습니다."));

		validateAccess(viewerId, journal.getMember().getId());
		return journal.getPhotosAsResponse(imageService);
	}

	@Transactional(readOnly = true)
	public List<PhotoListResponse> getDayPhotos(Long journalId, Integer dayNumber, Long viewerId) {
		Journal journal = journalRepository.findById(journalId)
			.orElseThrow(() -> new JournalNotFoundException("해당하는 여행일지가 없습니다."));

		validateAccess(viewerId, journal.getMember().getId());
		return journal.getDayPhotosAsResponse(dayNumber, imageService);
	}

	private void validateAccess(Long viewerId, Long memberId) {
		blockService.validateNotBlocked(viewerId, memberId);
	}

	@Transactional
	public void deleteJournal(Long journalId, Long memberId) {
		Journal journal = journalRepository.findBasicInfoById(journalId)
			.orElseThrow(() -> new JournalNotFoundException("해당하는 여행일지가 없습니다."));

		validateJournalOwnership(journal, memberId);

		exploreSeenJournalRepository.deleteByJournalId(journalId);

		memberStatisticsService.decreaseTravelDiaryCount(memberId);
		memberRegionStatisticsService.decreaseTravelDiaryCount(memberId, journal.getRegion());

		journalRepository.deleteImageInfoByJournalId(journalId);
		journalRepository.deleteById(journalId);
	}

	private void validateUpdateRequest(JournalUpdateRequest request) {
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

	private void validateJournalOwnership(Journal journal, Long memberId) {
		if (!journal.getMember().getId().equals(memberId)) {
			throw new BadRequestException("해당 여행일지를 수정/삭제할 권한이 없습니다.");
		}
	}

	private void updateStatisticsForCreate(Long memberId, String region) {
		memberStatisticsService.increaseTravelDiaryCount(memberId);
		memberRegionStatisticsService.increaseTravelDiaryCount(memberId, region);
	}

	@Transactional
	public void updateJournal(Long journalId, JournalUpdateRequest request, Long memberId) {
		validateUpdateRequest(request);
		request.validateBusinessRules();

		Journal journal = findJournalWithValidation(journalId, memberId);

		Set<String> photosToDelete = calculatePhotosToDelete(journal, request);
		String oldRegion = journal.getRegion();

		updateJournalBasicInfo(journal, request);

		List<JournalDay> updatedDays = createJournalDays(request.journalDays(), journal);
		journal.updateDaysDetail(updatedDays);

		photoService.processJournalPhotos(updatedDays, request.photoMetadataList());
		photoService.setJournalThumbnail(journal, updatedDays, request.thumbnailUploadId());

		journalRepository.save(journal);

		if (!photosToDelete.isEmpty()) {
			photoService.deletePhotosByUploadIds(photosToDelete);
		}

		if (!oldRegion.equals(request.region())) {
			memberRegionStatisticsService.decreaseTravelDiaryCount(memberId, oldRegion);
			memberRegionStatisticsService.increaseTravelDiaryCount(memberId, request.region());
		}
	}

	private void updateJournalBasicInfo(Journal journal, JournalUpdateRequest request) {
		List<HashTag> updatedHashTags = hashTagService.getOrCreateHashTags(request.hashTag());

		journal.updateJournalInfo(
			request.title(),
			request.region(),
			request.nights(),
			request.days(),
			request.startDate(),
			request.endDate(),
			request.description()
		);

		journal.updateHashTags(updatedHashTags);
	}

	private Set<String> calculatePhotosToDelete(Journal journal, JournalUpdateRequest request) {
		Set<String> existingPhotoUploadIds = journal.getDaysDetail().stream()
			.flatMap(day -> day.getPhotos().stream())
			.filter(Photo::hasImageInfo)
			.map(Photo::getUploadIdSafely)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());

		Set<String> newPhotoUploadIds = Optional.ofNullable(request.photoMetadataList())
			.orElse(Collections.emptyList())
			.stream()
			.map(PhotoMetadataRequest::uploadId)
			.collect(Collectors.toSet());

		return existingPhotoUploadIds.stream()
			.filter(uploadId -> !newPhotoUploadIds.contains(uploadId))
			.collect(Collectors.toSet());
	}

	@Transactional(readOnly = true)
	public JournalUpdateRequest getJournalForUpdate(Long journalId, Long memberId) {
		Journal journal = journalRepository.findById(journalId)
			.orElseThrow(() -> new JournalNotFoundException("해당하는 여행일지가 없습니다."));

		journal.validateOwnership(memberId);

		return JournalUpdateRequest.from(journal);
	}

	private Journal findJournalWithValidation(Long journalId, Long memberId) {
		Journal journal = journalRepository.findById(journalId)
			.orElseThrow(() -> new JournalNotFoundException("해당하는 여행일지가 없습니다."));

		journal.validateOwnership(memberId);

		return journal;
	}
}