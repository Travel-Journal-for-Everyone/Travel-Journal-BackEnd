package com.traveljournal.domain.photo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.photo.dto.PhotoMetadataResponse;
import com.traveljournal.domain.photo.service.PhotoMetadataService;
import com.traveljournal.global.data.ApiResponseHandler;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/photo")
@Tag(name = "Photo API", description = "사진 기능 API")
public class PhotoController {

	private final PhotoMetadataService photoMetadataService;

	@PostMapping("/metadata")
	public ResponseEntity<PhotoMetadataResponse> extractMetadata(
		@RequestParam("image") MultipartFile imageFile) {

		return ApiResponseHandler.getObjectSuccess(photoMetadataService.extractMetadata(imageFile));
	}
}
