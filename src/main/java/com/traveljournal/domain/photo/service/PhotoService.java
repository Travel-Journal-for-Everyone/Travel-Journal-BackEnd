package com.traveljournal.domain.photo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.Image.util.FilenameUtil;
import com.traveljournal.domain.photo.dto.PhotoUploadResponse;
import com.traveljournal.domain.photo.repository.PhotoRepository;
import com.traveljournal.global.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoService {
	private final ImageService imageService;
	private final PhotoRepository photoRepository;

	@Transactional
	public PhotoUploadResponse uploadJournalPhoto(MultipartFile photoFile, Long memberId) {
		imageService.validateImageFile(photoFile);

		String originalFilename = photoFile.getOriginalFilename();
		String extension = FilenameUtil.getExtension(originalFilename);
		String fileName = FilenameUtil.generateUniqueFileName("journal_photo", memberId, extension);

		byte[] originalImageBytes;
		try {
			originalImageBytes = photoFile.getBytes();
		} catch (IOException e) {
			throw new RuntimeException("이미지 파일을 읽는 중 오류가 발생했습니다.", e);
		}

		imageService.uploadToS3(originalImageBytes, fileName, extension, false);

		imageService.saveImageInfo(fileName, originalFilename);

		return PhotoUploadResponse.of(fileName, originalFilename);
	}

	@Transactional
	public List<PhotoUploadResponse> uploadJournalPhotos(List<MultipartFile> photoFiles, Long memberId) {
		List<PhotoUploadResponse> photoUploadResponses = new ArrayList<>();
		for (MultipartFile photoFile : photoFiles) {
			photoUploadResponses.add(uploadJournalPhoto(photoFile, memberId));
		}
		return photoUploadResponses;
	}

	public void existsByImageInfo(ImageInfo imageInfo, String filename) {
		if (photoRepository.existsByImageInfo(imageInfo)) {
			throw new BadRequestException("이미 등록된 사진입니다: " + filename);
		}
	}

}
