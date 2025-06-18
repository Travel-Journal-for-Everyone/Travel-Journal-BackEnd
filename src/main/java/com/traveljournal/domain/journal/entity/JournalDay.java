package com.traveljournal.domain.journal.entity;

import java.util.ArrayList;
import java.util.List;

import com.traveljournal.domain.photo.entity.Photo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "journal_day")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JournalDay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int dayNumber; // 1일차, 2일차 등

	@Column(length = 1000)
	private String description; // 1일차 관련 설명

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journal_id")
	private Journal journal;

	@OneToMany(mappedBy = "journalDay", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Photo> photos = new ArrayList<>();

	@OneToMany(mappedBy = "journalDay", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("spotOrder ASC")
	@Builder.Default
	private List<JournalDaySpot> spots = new ArrayList<>();

	public void addPhoto(Photo photo) {
		this.photos.add(photo);
		photo.assignJournalDay(this);
	}
}