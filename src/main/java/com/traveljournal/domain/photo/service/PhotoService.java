package com.traveljournal.domain.photo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.service.ImageInfoService;
import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.Image.util.FilenameUtil;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.journal.entity.JournalDay;
import com.traveljournal.domain.photo.dto.PhotoMetadataRequest;
import com.traveljournal.domain.photo.dto.PhotoUploadResponse;
import com.traveljournal.domain.photo.entity.Photo;
import com.traveljournal.domain.photo.repository.PhotoRepository;
import com.traveljournal.global.exception.ImageNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {
	private final ImageService imageService;
	private final ImageInfoService imageInfoService;
	private final PhotoRepository photoRepository;

	@Transactional
	public PhotoUploadResponse uploadJournalPhoto(MultipartFile photoFile, Long memberId) {
		imageService.validateImageFile(photoFile);

		String originalFilename = photoFile.getOriginalFilename();
		String extension = FilenameUtil.getExtension(originalFilename);
		String uploadId = FilenameUtil.generateUniqueFileName("journal_photo", memberId, extension);

		byte[] originalImageBytes;
		try {
			originalImageBytes = photoFile.getBytes();
		} catch (IOException e) {
			throw new RuntimeException("이미지 파일을 읽는 중 오류가 발생했습니다.", e);
		}

		imageService.uploadToS3(originalImageBytes, uploadId, extension, false);

		imageService.saveImageInfo(uploadId, originalFilename);

		return PhotoUploadResponse.of(uploadId, originalFilename);
	}

	@Transactional
	public List<PhotoUploadResponse> uploadJournalPhotos(List<MultipartFile> photoFiles, Long memberId) {
		List<PhotoUploadResponse> photoUploadResponses = new ArrayList<>();
		for (MultipartFile photoFile : photoFiles) {
			photoUploadResponses.add(uploadJournalPhoto(photoFile, memberId));
		}
		return photoUploadResponses;
	}

	@Transactional
	public void processJournalPhotos(List<JournalDay> journalDays, List<PhotoMetadataRequest> photoMetadataList) {
		if (photoMetadataList == null || photoMetadataList.isEmpty()) {
			return;
		}

		Set<String> uploadIds = photoMetadataList.stream()
			.map(PhotoMetadataRequest::uploadId)
			.collect(Collectors.toSet());

		Map<String, ImageInfo> imageInfoMap = imageInfoService.getImageInfosByUploadIds(uploadIds);

		Map<String, Photo> existingPhotoMap = photoRepository.findByImageInfoUploadIdInAsMap(uploadIds);

		validateImageExistence(uploadIds, imageInfoMap);

		processPhotosWithBatch(journalDays, photoMetadataList, imageInfoMap, existingPhotoMap);
	}

	private void validateImageExistence(Set<String> uploadIds, Map<String, ImageInfo> imageInfoMap) {
		Set<String> missingImages = uploadIds.stream()
			.filter(uploadId -> !imageInfoMap.containsKey(uploadId))
			.collect(Collectors.toSet());

		if (!missingImages.isEmpty()) {
			throw new ImageNotFoundException("업로드되지 않은 이미지가 있습니다: " + missingImages);
		}
	}

	private void processPhotosWithBatch(List<JournalDay> journalDays,
		List<PhotoMetadataRequest> photoMetadataList,
		Map<String, ImageInfo> imageInfoMap,
		Map<String, Photo> existingPhotoMap) {

		Set<String> processedUploadIds = new HashSet<>();
		int globalPhotoOrder = 1;

		for (JournalDay day : journalDays) {
			globalPhotoOrder = processDayPhotos(day, photoMetadataList, imageInfoMap,
				existingPhotoMap, processedUploadIds, globalPhotoOrder);
		}
	}

	private int processDayPhotos(JournalDay day, List<PhotoMetadataRequest> photoMetadataList,
		Map<String, ImageInfo> imageInfoMap, Map<String, Photo> existingPhotoMap,
		Set<String> processedUploadIds, int globalPhotoOrder) {

		int dayNum = day.getDayNumber();
		int daySpotOrder = 1;

		for (PhotoMetadataRequest meta : photoMetadataList) {
			if (meta.dayNumber() != dayNum) continue;
			if (!processedUploadIds.add(meta.uploadId())) continue;

			Photo photo = getOrCreatePhoto(meta, imageInfoMap, existingPhotoMap,
				globalPhotoOrder++, daySpotOrder++);
			assignPhotoToDay(photo, day);
		}

		return globalPhotoOrder;
	}

	private Photo getOrCreatePhoto(PhotoMetadataRequest meta, Map<String, ImageInfo> imageInfoMap,
		Map<String, Photo> existingPhotoMap, int photoOrder, int daySpotOrder) {

		ImageInfo imageInfo = imageInfoMap.get(meta.uploadId());
		Photo existingPhoto = existingPhotoMap.get(meta.uploadId());

		if (existingPhoto != null) {
			// 기존 사진 재사용 - 메타데이터 업데이트
			updatePhotoMetadata(existingPhoto, meta, photoOrder, daySpotOrder);
			log.debug("기존 사진 재사용: uploadId={}", meta.uploadId());
			return existingPhoto;
		} else {
			// 새 사진 생성
			Photo newPhoto = createNewPhoto(meta, imageInfo, photoOrder, daySpotOrder);
			log.debug("새 사진 생성: uploadId={}", meta.uploadId());
			return newPhoto;
		}
	}

	private void updatePhotoMetadata(Photo photo, PhotoMetadataRequest meta, int photoOrder, int daySpotOrder) {
		photo.updatePhotoMetadata(photoOrder, daySpotOrder, meta.description(),
			meta.address(), meta.latitude(), meta.longitude(),
			meta.getParsedTakenDateTime());
	}

	private Photo createNewPhoto(PhotoMetadataRequest meta, ImageInfo imageInfo, int photoOrder, int daySpotOrder) {
		return Photo.builder()
			.description(meta.description())
			.address(meta.address())
			.takenDateTime(meta.getParsedTakenDateTime())
			.latitude(meta.latitude())
			.longitude(meta.longitude())
			.imageInfo(imageInfo)
			.photoOrder(photoOrder)
			.daySpotOrder(daySpotOrder)
			.isThumbnail(false)
			.build();
	}

	private void assignPhotoToDay(Photo photo, JournalDay targetDay) {
		if (photo.getJournalDay() != null && !photo.getJournalDay().equals(targetDay)) {
			photo.getJournalDay().removePhoto(photo);
		}

		if (photo.getJournalDay() == null || !photo.getJournalDay().equals(targetDay)) {
			targetDay.addPhoto(photo);
		}
	}

	private int addPhotosToDay(JournalDay day, List<PhotoMetadataRequest> photoMetadataList,
		int globalPhotoOrder, Map<String, ImageInfo> imageInfoMap,
		Map<String, Photo> existingPhotoMap) {
		int dayNum = day.getDayNumber();
		int daySpotOrder = 1;

		for (PhotoMetadataRequest meta : photoMetadataList) {
			if (meta.dayNumber() != dayNum)
				continue;

			Photo photo = getOrCreatePhotoFromMaps(meta, globalPhotoOrder++, daySpotOrder++,
				imageInfoMap, existingPhotoMap);

			if (photo.getJournalDay() != null && !photo.getJournalDay().equals(day)) {
				photo.getJournalDay().removePhoto(photo);
			}

			if (photo.getJournalDay() == null) {
				day.addPhoto(photo);
			}
		}

		return globalPhotoOrder;
	}

	@Transactional
	public void deletePhotosByUploadIds(Set<String> uploadIds) {
		for (String uploadId : uploadIds) {
			try {
				deletePhotoByUploadId(uploadId);
				log.info("사진 삭제 완료: uploadId={}", uploadId);
			} catch (ImageNotFoundException e) {
				log.warn("삭제할 사진을 찾을 수 없음: uploadId={}", uploadId);
			} catch (Exception e) {
				log.error("사진 삭제 실패: uploadId={}, error={}", uploadId, e.getMessage(), e);
			}
		}
	}

	@Transactional
	public void deletePhotoByUploadId(String uploadId) {
		ImageInfo imageInfo = imageInfoService.getImageInfo(uploadId);

		Optional<Photo> photoToDelete = photoRepository.findByImageInfo(imageInfo);
		if (photoToDelete.isPresent()) {
			Photo photo = photoToDelete.get();

			if (photo.getJournalDay() != null) {
				photo.getJournalDay().removePhoto(photo);
			}

			photoRepository.delete(photo);
		}

		imageService.deleteImageFromS3(imageInfo.getUploadId());
		imageInfoService.deleteImageInfo(imageInfo);
	}

	@Transactional
	public void setJournalThumbnail(Journal journal, List<JournalDay> journalDays, String thumbnailUploadId) {
		Photo thumbnailPhoto = findThumbnailPhoto(journalDays, thumbnailUploadId);

		if (thumbnailPhoto != null) {
			journal.setThumbnail(thumbnailPhoto);
		}
	}

	private Photo findThumbnailPhoto(List<JournalDay> journalDays, String thumbnailUploadId) {
		Photo thumbnailPhoto = null;

		if (thumbnailUploadId != null) {
			thumbnailPhoto = journalDays.stream()
				.flatMap(day -> day.getPhotos().stream())
				.filter(photo -> photo.getImageInfo() != null)
				.filter(photo -> thumbnailUploadId.equals(photo.getImageInfo().getUploadId()))
				.findFirst()
				.orElse(null);
		}

		if (thumbnailPhoto == null && !journalDays.isEmpty()) {
			thumbnailPhoto = journalDays.stream()
				.flatMap(day -> day.getPhotos().stream())
				.filter(photo -> photo.getImageInfo() != null)
				.findFirst()
				.orElse(null);
		}

		return thumbnailPhoto;
	}

	private Photo getOrCreatePhotoFromMaps(PhotoMetadataRequest meta, int photoOrder, int daySpotOrder,
		Map<String, ImageInfo> imageInfoMap,
		Map<String, Photo> existingPhotoMap) {
		ImageInfo imageInfo = imageInfoMap.get(meta.uploadId());
		Photo existingPhoto = existingPhotoMap.get(meta.uploadId());

		if (imageInfo == null) {
			log.error("ImageInfo를 찾을 수 없습니다: uploadId={}", meta.uploadId());
			throw new ImageNotFoundException("이미지 정보를 찾을 수 없습니다: " + meta.uploadId());
		}

		if (existingPhoto != null) {
			existingPhoto.updatePhotoMetadata(
				photoOrder, daySpotOrder, meta.description(),
				meta.address(), meta.latitude(), meta.longitude(),
				meta.getParsedTakenDateTime()
			);
			log.debug("기존 사진 재사용: uploadId={}", meta.uploadId());
			return existingPhoto;
		} else {
			Photo photo = Photo.builder()
				.description(meta.description())
				.address(meta.address())
				.takenDateTime(meta.getParsedTakenDateTime())
				.latitude(meta.latitude())
				.longitude(meta.longitude())
				.imageInfo(imageInfo)
				.photoOrder(photoOrder)
				.daySpotOrder(daySpotOrder)
				.isThumbnail(false)
				.build();
			log.debug("새로운 사진 생성: uploadId={}", meta.uploadId());
			return photo;
		}
	}
}