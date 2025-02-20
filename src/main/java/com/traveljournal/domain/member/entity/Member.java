package com.traveljournal.domain.member.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 255)
	private String socialLoginId;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(length = 255)
	private String profileImageUrl;

	private LocalDateTime birthdate;

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

	public void updateNickname(String nickname) {
		this.nickname = nickname;
		this.isFirstLogin = false;
	}

	// UserDetails 메서드 구현
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(() -> "ROLE_USER");
	}

	@Override
	public String getPassword() {
		// Member 클래스에 비밀번호 필드가 없는 경우 아래를 수정해야 함
		return null; // 필드가 없다면 null, 아니면 password 필드 추가 및 반환
	}

	@Override
	public String getUsername() {
		return this.socialLoginId; // socialLoginId를 username으로 사용
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정 만료 여부 (true = 만료되지 않음)
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 계정 잠금 여부 (true = 잠금되지 않음)
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 자격 증명 만료 여부 (true = 만료되지 않음)
	}

	@Override
	public boolean isEnabled() {
		return !isDeleted; // 계정 활성화 여부 (삭제된 계정은 비활성화)
	}
}