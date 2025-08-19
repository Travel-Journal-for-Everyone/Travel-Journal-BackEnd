package com.traveljournal.global.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.traveljournal.global.exception.UnauthorizedException;
import com.traveljournal.global.security.service.CustomUserDetails;

/**
 * 현재 로그인한 사용자의 정보를 가져오는 유틸 클래스
 */
public class SecurityUtil {

	/**
	 * 현재 로그인한 사용자의 memberId 가져오기
	 * @return Long memberId
	 * @throws IllegalStateException 로그인 정보가 없거나 잘못된 경우
	 */
	public static Long getCurrentMemberId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
			throw new UnauthorizedException("올바른 사용자 정보가 아닙니다.");
		}
		return userDetails.getMemberId();
	}

	public static Long getCurrentMemberIdOrNull() {
		try {
			return getCurrentMemberId();
		} catch (Exception e) {
			return null;
		}
	}
}