package com.traveljournal.domain.photo.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.journal.entity.JournalDay;
import com.traveljournal.global.exception.BadRequestException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "photo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "photo_order", nullable = false)
	private Integer photoOrder;

	@Column(name = "day_spot_order", nullable = false)
	private Integer daySpotOrder;

	@Column(length = 1000)
	private String description;

	@Column(length = 255)
	private String address;

	@Column(nullable = false)
	private LocalDateTime takenDateTime;

	private Double latitude;

	private Double longitude;

	@Column(name = "is_thumbnail", nullable = false)
	private Boolean isThumbnail = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journal_day_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private JournalDay journalDay;

	@OneToOne(fetch = FetchType.LAZY,
		cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "image_info_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private ImageInfo imageInfo;

	@Builder
	public Photo(String description, String address, LocalDateTime takenDateTime,
		Double latitude, Double longitude, ImageInfo imageInfo, Integer photoOrder, Integer daySpotOrder, Boolean isThumbnail) {
		this.description = description;
		this.address = address;
		this.takenDateTime = takenDateTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageInfo = imageInfo;
		this.photoOrder = photoOrder;
		this.daySpotOrder = daySpotOrder;
		this.isThumbnail = isThumbnail != null ? isThumbnail : false;
	}

	public void setAsThumbnail() {
		this.isThumbnail = true;
	}

	public void unsetAsThumbnail() {
		this.isThumbnail = false;
	}

	public void assignJournalDay(JournalDay journalDay) {
		if (journalDay == null) {
			throw new BadRequestException("여행일차는 null일 수 없습니다.");
		}
		if (this.journalDay != null && !this.journalDay.equals(journalDay)) {
			throw new BadRequestException("이미 다른 여행일차에 할당된 사진입니다.");
		}
		this.journalDay = journalDay;
	}

	public void removeFromJournalDay() {
		this.journalDay = null;
	}

	public void updatePhotoMetadata(int photoOrder, int daySpotOrder, String description,
		String address, Double latitude, Double longitude,
		LocalDateTime takenDateTime) {
		validateOrder(photoOrder, daySpotOrder);

		this.photoOrder = photoOrder;
		this.daySpotOrder = daySpotOrder;
		this.description = description;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.takenDateTime = takenDateTime;
	}

	private void validateOrder(int photoOrder, int daySpotOrder) {
		if (photoOrder < 1 || daySpotOrder < 1) {
			throw new BadRequestException("사진 순서는 1 이상이어야 합니다.");
		}
	}

	public int getDayNumber() {
		return journalDay != null ? journalDay.getDayNumber() : 0;
	}

	public boolean hasImageInfo() {
		return imageInfo != null;
	}

	public String getUploadIdSafely() {
		return hasImageInfo() ? imageInfo.getUploadId() : null;
	}
}
