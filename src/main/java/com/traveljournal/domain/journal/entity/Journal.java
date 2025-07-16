package com.traveljournal.domain.journal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.BatchSize;

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.photo.dto.PhotoListResponse;
import com.traveljournal.domain.photo.entity.Photo;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.UnauthorizedException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Journal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Journal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	@Column(length = 100)
	private String region;

	private Long nights;
	private Long days;

	private String startDate;
	private String endDate;

	private LocalDateTime createdAt;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinTable(
		name = "journal_hashtag",
		joinColumns = @JoinColumn(name = "journal_id"),
		inverseJoinColumns = @JoinColumn(name = "hashtag_id")
	)
	@Builder.Default
	@BatchSize(size = 10)
	private List<HashTag> hashTags = new ArrayList<>();

	@OneToMany(mappedBy = "journal",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	@Builder.Default
	@BatchSize(size = 10)
	private List<JournalDay> daysDetail = new ArrayList<>();

	@Column(name = "random_index")
	private Double randomIndex;

	@PrePersist
	public void prePersist() {
		if (randomIndex == null) {
			randomIndex = Math.random();
		}
	}

	public void addDay(JournalDay day) {
		addDayInternal(day);
	}

	public List<PhotoListResponse> getPhotosAsResponse(ImageService imageService) {
		return this.daysDetail.stream()
			.flatMap(day -> day.getPhotos().stream())
			.sorted(Comparator.comparing(Photo::getPhotoOrder))
			.map(photo -> PhotoListResponse.from(photo,
				imageService.getImageUrl(photo.getImageInfo().getUploadId())))
			.toList();
	}

	public List<PhotoListResponse> getDayPhotosAsResponse(Integer dayNumber, ImageService imageService) {
		return this.daysDetail.stream()
			.filter(day -> Objects.equals(day.getDayNumber(), dayNumber))
			.flatMap(day -> day.getPhotos().stream())
			.sorted(Comparator.comparing(Photo::getDaySpotOrder))
			.map(photo -> PhotoListResponse.from(photo,
				imageService.getImageUrl(photo.getImageInfo().getUploadId())))
			.toList();
	}

	public void validateOwnership(Long memberId) {
		if (memberId == null) {
			throw new IllegalArgumentException("회원 ID는 null일 수 없습니다.");
		}
		if (this.member == null || !Objects.equals(this.member.getId(), memberId)) {
			throw new UnauthorizedException("해당 여행일지를 수정할 권한이 없습니다.");
		}
	}

	private void addDayInternal(JournalDay day) {
		if (day == null) {
			throw new IllegalArgumentException("여행일차는 null일 수 없습니다.");
		}
		this.daysDetail.add(day);
		day.assignToJournal(this);
	}

	private void removeDayInternal(JournalDay day) {
		if (day != null) {
			this.daysDetail.remove(day);
			day.removeFromJournal();
		}
	}

	public void updateDaysDetail(List<JournalDay> newDays) {
		new ArrayList<>(this.daysDetail).forEach(this::removeDayInternal);

		if (newDays != null && !newDays.isEmpty()) {
			validateDayNumbers(newDays);
			newDays.forEach(this::addDayInternal);
		}
	}

	private void validateDayNumbers(List<JournalDay> days) {
		if (days.size() != this.days.intValue()) {
			throw new BadRequestException("일차 수가 여행 기간과 일치하지 않습니다.");
		}

		long distinctDayNumbers = days.stream()
			.mapToInt(JournalDay::getDayNumber)
			.distinct()
			.count();

		if (distinctDayNumbers != days.size()) {
			throw new BadRequestException("중복된 일차 번호가 있습니다.");
		}
	}

	public void updateJournalInfo(String title, String region, Long nights, Long days,
		String startDate, String endDate, String description) {
		validateJournalInfoInput(title, region, nights, days);

		this.title = title.trim();
		this.region = region.trim();
		this.nights = nights;
		this.days = days;
		this.startDate = startDate != null ? startDate.trim() : this.startDate;
		this.endDate = endDate != null ? endDate.trim() : this.endDate;
		this.description = description != null ? description.trim() : this.description;
	}

	private void validateJournalInfoInput(String title, String region, Long nights, Long days) {
		if (title == null || title.trim().isEmpty()) {
			throw new BadRequestException("제목은 필수입니다.");
		}
		if (region == null || region.trim().isEmpty()) {
			throw new BadRequestException("지역은 필수입니다.");
		}
		if (nights == null || nights < 0) {
			throw new BadRequestException("박수는 0 이상이어야 합니다.");
		}
		if (days == null || days < 1) {
			throw new BadRequestException("일수는 1 이상이어야 합니다.");
		}
		if (days != nights + 1) {
			throw new BadRequestException("일수와 박수가 일치하지 않습니다. (일수 = 박수 + 1)");
		}
	}

	public void updateHashTags(List<HashTag> newHashTags) {
		this.hashTags.clear();
		if (newHashTags != null && !newHashTags.isEmpty()) {
			List<HashTag> distinctHashTags = newHashTags.stream()
				.distinct()
				.toList();
			this.hashTags.addAll(distinctHashTags);
		}
	}

	public Photo getThumbnailPhoto() {
		return this.daysDetail.stream()
			.flatMap(day -> day.getPhotos().stream())
			.filter(Photo::getIsThumbnail)
			.findFirst()
			.orElse(getFirstPhoto()); // fallback
	}

	public String getThumbnailUrl(ImageService imageService) {
		Photo thumbnailPhoto = getThumbnailPhoto();
		if (thumbnailPhoto != null) {
			return imageService.getImageUrl(thumbnailPhoto.getImageInfo().getUploadId());
		}
		return null;
	}

	public String getThumbnailAddress() {
		Photo thumbnailPhoto = getThumbnailPhoto();
		return thumbnailPhoto != null ? thumbnailPhoto.getAddress() : null;
	}

	private Photo getFirstPhoto() {
		return this.daysDetail.stream()
			.filter(day -> !day.getPhotos().isEmpty())
			.findFirst()
			.map(day -> day.getPhotos().get(0))
			.orElse(null);
	}

	public void setThumbnail(Photo photo) {
		// 기존 썸네일 해제
		this.daysDetail.stream()
			.flatMap(day -> day.getPhotos().stream())
			.filter(Photo::getIsThumbnail)
			.forEach(Photo::unsetAsThumbnail);

		// 새 썸네일 설정
		photo.setAsThumbnail();
	}
}
