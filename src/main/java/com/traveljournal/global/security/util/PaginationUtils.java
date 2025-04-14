package com.traveljournal.global.security.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class PaginationUtils {

	/**
	 * 리스트 데이터를 페이지네이션 처리합니다.
	 *
	 * @param allData 전체 데이터 리스트
	 * @param page 요청 페이지 (0부터 시작)
	 * @param size 페이지 크기
	 * @return 페이지네이션 처리된 Page 객체
	 */
	public static <T> Page<T> getPagedList(List<T> allData, int page, int size) {
		int start = page * size;
		int end = Math.min(start + size, allData.size());

		if (start > allData.size()) {
			return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), allData.size());
		}

		List<T> pageData = allData.subList(start, end);
		return new PageImpl<>(pageData, PageRequest.of(page, size), allData.size());
	}
}
