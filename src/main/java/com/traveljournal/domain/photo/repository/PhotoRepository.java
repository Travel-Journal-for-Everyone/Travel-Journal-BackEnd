package com.traveljournal.domain.photo.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.photo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	@Query("SELECT p FROM Photo p WHERE p.imageInfo.uploadId IN :uploadIds")
	List<Photo> findByImageInfoUploadIdIn(@Param("uploadIds") Set<String> uploadIds);

	default Map<String, Photo> findByImageInfoUploadIdInAsMap(Set<String> uploadIds) {
		return findByImageInfoUploadIdIn(uploadIds).stream()
			.collect(Collectors.toMap(
				photo -> photo.getImageInfo().getUploadId(),
				photo -> photo
			));
	}

	@Modifying
	@Query("DELETE FROM Photo p WHERE p.imageInfo = :imageInfo")
	void deleteByImageInfo(@Param("imageInfo") ImageInfo imageInfo);

	Optional<Photo> findByImageInfo(ImageInfo imageInfo);
}