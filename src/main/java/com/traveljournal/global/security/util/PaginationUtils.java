package com.traveljournal.global.security.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
		int start = (int)pageable.getOffset();
		int end = Math.min(start + pageable.getPageSize(), allData.size());

		if (start > allData.size()) {
			return new PageImpl<>(new ArrayList<>(), pageable, allData.size());
		}

		List<T> pageData = allData.subList(start, end);
		return new PageImpl<>(pageData, pageable, allData.size());
	}
}
