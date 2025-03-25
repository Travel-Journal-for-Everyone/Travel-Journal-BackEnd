package com.traveljournal.domain.Image.util;

import java.util.UUID;

public class FilenameUtil {
	/**
	 * 고유한 파일명 생성
	 * UUID를 사용하여 중복 가능성을 최소화
	 *
	 * @param prefix 파일명 접두사 (예: profile, post 등)
	 * @param id 연관된 ID (예: memberId, postId 등)
	 * @param extension 파일 확장자
	 * @return 생성된 고유 파일명
	 */
	public static String generateUniqueFileName(String prefix, Long id, String extension) {
		String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
		return String.format("%s_%d_%d_%s.%s",
			prefix,
			id,
			System.currentTimeMillis(),
			uuid,
			extension.toLowerCase());
	}

	/**
	 * 파일 확장자 추출
	 *
	 * @param filename 원본 파일명
	 * @return 파일 확장자 (기본값: jpg)
	 */
	public static String getExtension(String filename) {
		return java.util.Optional.ofNullable(filename)
			.filter(f -> f.contains("."))
			.map(f -> f.substring(filename.lastIndexOf(".") + 1).toLowerCase())
			.orElse("jpg");
	}
}