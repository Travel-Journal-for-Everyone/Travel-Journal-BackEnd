package com.traveljournal.domain.Image.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.Image.repository.ImageInfoRepository;
import com.traveljournal.domain.Image.util.FilenameUtil;
import com.traveljournal.global.exception.ImageDeleteException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ImageService {
	private final S3Client s3Client;
	private final ImageInfoRepository imageInfoRepository;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucketName;

	@Value("${custom.aws.upload-path}")
	private String uploadPath;

	@Value("${custom.aws.resized-path}")
	private String resizedPath;

	@Value("${custom.aws.default-profile-image}")
	private String defaultProfileImage;

	// 이미지 크기 정의
	private static final int PROFILE_ORIGINAL_SIZE = 800; // 원본 이미지 최대 크기

	/**
	 * 프로필 이미지 업로드 및 최적화
	 */
	@Transactional
	public String uploadProfileImage(MultipartFile imageFile, Long memberId) throws IOException {
		validateImageFile(imageFile);

		// 파일명 생성
		String originalFilename = imageFile.getOriginalFilename();
		String extension = FilenameUtil.getExtension(originalFilename);
		String uploadId = FilenameUtil.generateUniqueFileName("profile", memberId, extension);

		// 원본 이미지 저장
		byte[] originalImageBytes = imageFile.getBytes();
		String originalImagePath = uploadToS3(originalImageBytes, uploadId, extension, false);

		// // 리사이즈된 이미지 생성 및 저장
		// byte[] resizedImageBytes = resizeImage(imageFile);
		// uploadToS3(resizedImageBytes, fileName, extension, true);

		// 이미지 정보 저장
		saveImageInfo(uploadId, originalFilename);

		// 원본 이미지 경로 반환
		return originalImagePath;
	}

	/**
	 * 이미지 정보를 데이터베이스에 저장
	 */
	public void saveImageInfo(String uploadId, String uploadFilename) {
		ImageInfo imageInfo = ImageInfo.builder()
			.uploadId(uploadId)
			.uploadFilename(uploadFilename)
			.build();

		imageInfoRepository.save(imageInfo);
	}

	/**
	 * 이미지 파일 유효성 검사
	 */
	public void validateImageFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
		}
	}

	/**
	 * S3에 이미지 업로드 (SDK V2 방식)
	 * @param imageBytes 업로드할 이미지 바이트 배열
	 * @param uploadId 파일 이름
	 * @param extension 파일 확장자
	 * @param isResized 리사이즈된 이미지인지 여부
	 * @return 업로드된 이미지의 URL
	 */
	public String uploadToS3(byte[] imageBytes, String uploadId, String extension, boolean isResized) {
		// 리사이즈된 이미지는 resizedPath에, 원본 이미지는 uploadPath에 저장
		String path = isResized ? resizedPath : uploadPath;
		String key = path + "/" + uploadId;

		try {
			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.contentType("image/" + extension)
				// .acl(ObjectCannedACL.PUBLIC_READ)
				.build();

			s3Client.putObject(
				putObjectRequest,
				RequestBody.fromBytes(imageBytes)
			);

			return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
		} catch (S3Exception e) {
			throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다: " + e.awsErrorDetails().errorMessage(), e);
		}
	}

	public String getDefaultProfileImageUrl() {
		return String.format("https://%s.s3.amazonaws.com/%s", bucketName, defaultProfileImage);
	}

	public String getImageUrl(String filename) {
		String key = uploadPath + "/" + filename;
		return String.format("https://%s.s3.amazonaws.com/%s", bucketName, key);
	}

	public void deleteImageFromS3(String uploadId) {
		try {
			// S3에서 원본 파일 삭제
			deleteS3Object(uploadPath + "/" + uploadId);

			// 썸네일이 있다면 썸네일도 삭제
			try {
				deleteS3Object(resizedPath + "/" + uploadId);
			} catch (Exception e) {
				log.warn("썸네일 삭제 실패 (무시): {}", uploadId);
			}

			log.info("S3 파일 삭제 완료: {}", uploadId);
		} catch (Exception e) {
			log.error("S3 파일 삭제 실패: {}", uploadId, e);
			throw new ImageDeleteException("S3 파일 삭제에 실패했습니다: " + uploadId);
		}
	}

	private void deleteS3Object(String key) {
		try {
			DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

			s3Client.deleteObject(deleteObjectRequest);
		} catch (S3Exception e) {
			throw new RuntimeException("S3 객체 삭제 실패: " + e.awsErrorDetails().errorMessage(), e);
		}
	}
}