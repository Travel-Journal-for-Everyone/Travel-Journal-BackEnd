package com.traveljournal.domain.photo.dto;

public record PhotoUploadResponse(
	String uploadId,
	String uploadFilename
) {
	public static PhotoUploadResponse of(String uploadId, String uploadFilename) {
		return new PhotoUploadResponse(uploadId, uploadFilename);
	}
}