package com.traveljournal.domain.Image.service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.repository.ImageInfoRepository;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.ImageNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageInfoService {

	private final ImageInfoRepository imageInfoRepository;

	@Transactional(readOnly = true)
	public ImageInfo getImageInfo(String uploadId) {
		if (uploadId == null || uploadId.trim().isEmpty()) {
			throw new BadRequestException("업로드 ID는 필수입니다.");
		}

		return imageInfoRepository.findByUploadId(uploadId.trim())
			.orElseThrow(() -> new ImageNotFoundException("이미지 정보를 찾을 수 없습니다: " + uploadId));
	}

	@Transactional
	public void deleteImageInfo(ImageInfo imageInfo) {
		if (imageInfo == null) {
			throw new BadRequestException("삭제할 이미지 정보가 없습니다.");
		}

		try {
			imageInfoRepository.delete(imageInfo);
			log.info("ImageInfo 삭제 완료: uploadId={}", imageInfo.getUploadId());
		} catch (Exception e) {
			log.error("ImageInfo 삭제 실패: uploadId={}", imageInfo.getUploadId(), e);
			throw new BadRequestException("이미지 정보 삭제 중 오류가 발생했습니다.");
		}
	}

	public Map<String, ImageInfo> getImageInfosByUploadIds(Set<String> uploadIds) {
		return imageInfoRepository.findByUploadIdIn(uploadIds).stream()
			.collect(Collectors.toMap(
				ImageInfo::getUploadId,
				imageInfo -> imageInfo
			));
	}
}