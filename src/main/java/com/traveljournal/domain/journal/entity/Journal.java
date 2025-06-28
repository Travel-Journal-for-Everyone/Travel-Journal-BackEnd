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

	@Column(length = 512)
	private String thumbnailUrl;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToMany
	@JoinTable(
		name = "journal_hashtag",
		joinColumns = @JoinColumn(name = "journal_id"),
		inverseJoinColumns = @JoinColumn(name = "hashtag_id")
	)
	@Builder.Default
	@BatchSize(size = 10)
	private List<HashTag> hashTags = new ArrayList<>();

	@OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
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
		this.daysDetail.add(day);
	}

	public void updateThumbnailUrl(String url) {
		if (url != null && !url.trim().isEmpty()) {
			this.thumbnailUrl = url.trim();
		}
	}

	public List<PhotoListResponse> getPhotosAsResponse(ImageService imageService) {
		return this.daysDetail.stream()
			.flatMap(day -> day.getPhotos().stream())
			.sorted(Comparator.comparing(Photo::getPhotoOrder))
			.map(photo -> PhotoListResponse.from(photo,
				imageService.getImageUrl(photo.getImageInfo().getFilename())))
			.toList();
	}

	public boolean hasPhotos() {
		return this.daysDetail.stream()
			.anyMatch(day -> !day.getPhotos().isEmpty());
	}

	public List<PhotoListResponse> getDayPhotosAsResponse(Integer dayNumber, ImageService imageService) {
		return this.daysDetail.stream()
			.filter(day -> Objects.equals(day.getDayNumber(), dayNumber))
			.flatMap(day -> day.getPhotos().stream())
			.sorted(Comparator.comparing(Photo::getDaySpotOrder))
			.map(photo -> PhotoListResponse.from(photo,
				imageService.getImageUrl(photo.getImageInfo().getFilename())))
			.toList();
	}
}
