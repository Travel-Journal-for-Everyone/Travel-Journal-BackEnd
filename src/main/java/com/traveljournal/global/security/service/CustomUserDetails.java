package com.traveljournal.global.security.service;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.traveljournal.domain.member.entity.Member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security에서 사용할 CustomUserDetails
 */
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
	private final Long memberId;  // 추가
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(Member member) {
		this.memberId = member.getId();
		this.email = member.getEmail();
		this.authorities = Collections.singletonList(() -> "ROLE_USER"); // 기본 권한 설정
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return ""; // OAuth2 로그인 시 비밀번호 사용 안 함
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}