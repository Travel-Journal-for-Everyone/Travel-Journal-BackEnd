package com.traveljournal.domain.Image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.Image.entity.ImageInfo;

public interface ImageInfoRepository extends JpaRepository<ImageInfo, Long> {
	Optional<ImageInfo> findByFilename(String filename);
}
