package com.traveljournal.domain.journal.entity;

import java.util.ArrayList;
import java.util.List;

import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Journal")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Journal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String region;
	private Long nights;
	private Long days;
	private String startDate;
	private String endDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@ManyToMany
	@JoinTable(
		name = "journal_hashtag",
		joinColumns = @JoinColumn(name = "journal_id"),
		inverseJoinColumns = @JoinColumn(name = "hashtag_id")
	)
	private List<HashTag>  hashTags = new ArrayList<>();
}
