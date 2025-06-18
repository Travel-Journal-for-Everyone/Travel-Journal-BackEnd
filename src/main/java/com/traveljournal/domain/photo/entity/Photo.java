package com.traveljournal.domain.photo.entity;

import java.time.LocalDateTime;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.journal.entity.JournalDay;

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

	@Column(length = 1000)
	private String description;

	@Column(length = 255)
	private String placeName;

	@Column(nullable = false)
	private LocalDateTime takenDateTime;

	private Double latitude;

	private Double longitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journal_day_id")
	private JournalDay journalDay;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "image_info_id")
	private ImageInfo imageInfo;

	@Builder
	public Photo(String description, String placeName, LocalDateTime takenDateTime,
		Double latitude, Double longitude, ImageInfo imageInfo) {
		this.description = description;
		this.placeName = placeName;
		this.takenDateTime = takenDateTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.imageInfo = imageInfo;
	}

	public void assignJournalDay(JournalDay journalDay) {
		this.journalDay = journalDay;
	}
}
