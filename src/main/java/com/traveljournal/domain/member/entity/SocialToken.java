package com.traveljournal.domain.member.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
	@UniqueConstraint(columnNames = {"member_id", "provider"})
})
public class SocialToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String providerId;

	@JoinColumn(name = "member_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Member member;

	@Column(length = 1024)
	private String refreshToken;

	@Enumerated(EnumType.STRING)
	private SocialProvider provider;

	private LocalDateTime expiryDate;

	private LocalDateTime lastUpdatedAt;

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private Platform platform; // WEB, IOS, ANDROID 등

	@Builder
	public SocialToken(Member member, String refreshToken, SocialProvider provider, LocalDateTime expiryDate, String providerId, Platform platform) {
		this.member = member;
		this.refreshToken = refreshToken;
		this.provider = provider;
		this.expiryDate = expiryDate;
		this.lastUpdatedAt = LocalDateTime.now();
		this.providerId = providerId;
		this.platform = platform;
	}

	public void updateRefreshToken(String refreshToken, LocalDateTime expiryDate, Platform platform) {
		this.refreshToken = refreshToken;
		this.expiryDate = expiryDate;
		this.lastUpdatedAt = LocalDateTime.now();
		this.platform = platform;
	}

	public boolean isExpired() {
		return expiryDate != null && expiryDate.isBefore(LocalDateTime.now());
	}
}