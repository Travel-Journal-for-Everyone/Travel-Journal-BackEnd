package com.traveljournal.domain.Image.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.Image.entity.ImageInfo;

public interface ImageInfoRepository extends JpaRepository<ImageInfo, Long> {
	Optional<ImageInfo> findByUploadId(String uploadId);

	List<ImageInfo> findByUploadIdIn(Set<String> uploadIds);
}
