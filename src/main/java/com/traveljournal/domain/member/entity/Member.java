package com.traveljournal.domain.member.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	private String email;

	@Column(nullable = false, unique = true, length = 255)
	private String providerId;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(length = 255)
	private String profileImageUrl;

	private LocalDate birthdate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountScope accountScope;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createAt;

	@Column(nullable = false)
	private Boolean isDeleted = false;

	@Column(length = 20)
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SocialProvider socialProvider;

	private Boolean isFirstLogin = true;

	@PrePersist
	public void prePersist() {
		this.createAt = LocalDateTime.now();
	}

	@Builder
	public Member(String providerId, String email, String nickname, String profileImageUrl, LocalDate birthdate,
		AccountScope accountScope, String phoneNumber, SocialProvider socialProvider) {
		this.providerId = providerId;
		this.email = email;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.birthdate = birthdate;
		this.accountScope = accountScope != null ? accountScope : AccountScope.PUBLIC;
		this.phoneNumber = phoneNumber;
		this.socialProvider = socialProvider;
		this.isDeleted = false;
		this.isFirstLogin = true;
	}

	// 첫 로그인 상태 변경
	public void completeFirstLogin(String nickname, AccountScope accountScope) {
		this.isFirstLogin = false;
		this.nickname = nickname;
		this.accountScope = accountScope;
	}

	// 회원 정보 업데이트
	public void updateProfile(String nickname, String profileImageUrl,
		LocalDate birthdate, AccountScope accountScope,
		String phoneNumber) {
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
		this.birthdate = birthdate;
		this.accountScope = accountScope;
		this.phoneNumber = phoneNumber;
	}

	// 회원 삭제 (soft delete)
	public void delete() {
		this.isDeleted = true;
	}
}