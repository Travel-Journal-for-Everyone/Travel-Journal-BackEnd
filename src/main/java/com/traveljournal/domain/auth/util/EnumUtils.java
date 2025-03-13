package com.traveljournal.domain.auth.util;

import com.traveljournal.domain.member.entity.SocialProvider;

public class EnumUtils {
	public static SocialProvider toSocialProvider(String socialProviderStr) {
		try {
			return SocialProvider.valueOf(socialProviderStr.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid SocialProvider: " + socialProviderStr, e);
		}
	}
}