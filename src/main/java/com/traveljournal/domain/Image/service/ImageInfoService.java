package com.traveljournal.domain.Image.service;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.repository.ImageInfoRepository;
import com.traveljournal.global.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageInfoService {

	private final ImageInfoRepository imageInfoRepository;
	public ImageInfo getImageInfo(String filename) {

		return imageInfoRepository.findByFilename((filename))
			.orElseThrow(() -> new BadRequestException("이미지 정보가 없습니다: " + filename));
	}
}
