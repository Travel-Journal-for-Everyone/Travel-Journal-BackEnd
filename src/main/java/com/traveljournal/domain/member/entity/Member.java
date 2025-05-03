package com.traveljournal.domain.member.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.place.entity.Place;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String providerId;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(length = 255)
	private String profileImageUrl;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountScope accountScope;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialProvider socialProvider;

	private Boolean isFirstLogin = true;

	@OneToMany(mappedBy = "fromMember", fetch = FetchType.LAZY)
	private List<Follow> followings;

	@OneToMany(mappedBy = "toMember", fetch = FetchType.LAZY)
	private List<Follow> followers;

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<Place> places = new ArrayList<>();

	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<Journal> journals = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		this.createAt = LocalDateTime.now();
	}

	@Builder
	public Member(String providerId, String nickname, String profileImageUrl,
		AccountScope accountScope, SocialProvider socialProvider) {
		this.providerId = providerId;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.accountScope = accountScope != null ? accountScope : AccountScope.PUBLIC;
		this.socialProvider = socialProvider;
		this.isFirstLogin = true;
	}

	public void updateProfile(String nickname, AccountScope accountScope, String profileImageUrl, boolean memberDefaultImage) {
		this.isFirstLogin = false;
		this.nickname = nickname;
		this.accountScope = accountScope;
		if (profileImageUrl != null && !profileImageUrl.isEmpty() && !profileImageUrl.equals("null") && !memberDefaultImage) {
			this.profileImageUrl = profileImageUrl;
		}
		else if (memberDefaultImage) {
			this.profileImageUrl = profileImageUrl;
		}

	}
}