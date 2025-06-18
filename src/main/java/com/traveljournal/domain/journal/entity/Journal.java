package com.traveljournal.domain.journal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.member.entity.Member;

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
	private List<HashTag> hashTags = new ArrayList<>();

	@OneToMany(mappedBy = "journal", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
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

	public void setThumbnailUrl(String url) {
		this.thumbnailUrl = url;
	}
}
