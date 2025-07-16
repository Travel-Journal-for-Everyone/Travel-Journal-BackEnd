package com.traveljournal.domain.journal.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.traveljournal.domain.photo.entity.Photo;
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
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Journal journal;

	@OneToMany(mappedBy = "journalDay",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	@OrderBy("photoOrder ASC")
	@Builder.Default
	@BatchSize(size = 10)
	private List<Photo> photos = new ArrayList<>();

	@OneToMany(mappedBy = "journalDay",
		cascade = CascadeType.ALL,
		orphanRemoval = true,
		fetch = FetchType.LAZY)
	@OrderBy("spotOrder ASC")
	@Builder.Default
	@BatchSize(size = 10)
	private List<JournalDaySpot> spots = new ArrayList<>();

	public void assignToJournal(Journal journal) {
		if (journal == null) {
			throw new BadRequestException("여행일지는 null일 수 없습니다.");
		}
		if (this.journal != null && !this.journal.equals(journal)) {
			throw new BadRequestException("이미 다른 여행일지에 할당된 일차입니다.");
		}
		this.journal = journal;
	}

	public void removeFromJournal() {
		this.journal = null;
	}

	public void addPhoto(Photo photo) {
		if (photo == null) {
			throw new IllegalArgumentException("사진은 null일 수 없습니다.");
		}
		this.photos.add(photo);
		photo.assignJournalDay(this);
	}

	public void removePhoto(Photo photo) {
		if (photo != null) {
			this.photos.remove(photo);
			photo.removeFromJournalDay();
		}
	}

	public static JournalDay createForJournal(Journal journal, int dayNumber, String description) {
		if (journal == null) {
			throw new IllegalArgumentException("여행일지는 null일 수 없습니다.");
		}

		JournalDay journalDay = JournalDay.builder()
			.dayNumber(dayNumber)
			.description(description != null ? description.trim() : null)
			.build();

		journalDay.assignToJournal(journal);
		return journalDay;
	}
}