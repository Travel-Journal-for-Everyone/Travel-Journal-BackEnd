package com.traveljournal.global.util;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {

	/**
	 * 리스트 데이터를 페이지네이션 처리합니다.
	 *
	 * @param allData 전체 데이터 리스트
	 * @param pageable 페이지 정보 (페이지 번호, 크기, 정렬 등)
	 * @return 페이지네이션 처리된 Page 객체
	 */
	public static <T> Page<T> getPagedList(List<T> allData, Pageable pageable) {
		if (allData == null || allData.isEmpty() || pageable == null) {
			return Page.empty(pageable != null ? pageable : PageRequest.of(0, 10));
		}

		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), allData.size());

		if (start >= allData.size()) {
			return new PageImpl<>(Collections.emptyList(), pageable, allData.size());
		}

		List<T> pageData = allData.subList(start, end);
		return new PageImpl<>(pageData, pageable, allData.size());
	}
}
