package com.traveljournal.global.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traveljournal.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 정보를 로드하는 서비스
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return memberRepository.findBySocialLoginId(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}