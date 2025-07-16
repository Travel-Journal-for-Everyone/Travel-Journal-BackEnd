package com.traveljournal.domain.Image.entity;

import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.BatchSize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@BatchSize(size = 10)
public class ImageInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Unique
	private String uploadId;

	@Column(nullable = false)
	private String uploadFilename;

	@Builder
	public ImageInfo(String uploadId, String uploadFilename) {
		this.uploadId = uploadId;
		this.uploadFilename = uploadFilename;
	}
}
