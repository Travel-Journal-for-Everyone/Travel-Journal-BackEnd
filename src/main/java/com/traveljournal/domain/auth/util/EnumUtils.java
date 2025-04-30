package com.traveljournal.domain.auth.util;

import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.global.exception.BadRequestException;

public class EnumUtils {
	public static SocialProvider toSocialProvider(String socialProviderStr) {
		try {
			return SocialProvider.valueOf(socialProviderStr.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("지원하지 않는 소셜 로그인 제공자입니다 : " + socialProviderStr);
		}
	}
}