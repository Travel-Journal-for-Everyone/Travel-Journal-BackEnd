package com.traveljournal.domain.photo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.photo.dto.PhotoMetadataResponse;
import com.traveljournal.domain.photo.service.PhotoMetadataService;
import com.traveljournal.global.data.ApiResponseHandler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/photo")
@Tag(name = "Photo API", description = "사진 기능 API")
public class PhotoController {

	private final PhotoMetadataService photoMetadataService;

	@PostMapping(value = "/metadata",
		consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(
		summary = "사진 메타데이터 추출",
		description = "업로드한 사진 파일에서 촬영일시, 주소, 위도, 경도 등 메타데이터를 추출합니다.",
		security = @SecurityRequirement(name = "bearer-key")
	)
	public ResponseEntity<PhotoMetadataResponse> extractMetadata(
		@Parameter(
			description = "메타데이터를 추출할 이미지 파일",
			required = true
		)
		@RequestPart("image") MultipartFile imageFile) {

		return ApiResponseHandler.getObjectSuccess(photoMetadataService.extractMetadata(imageFile));
	}
}
