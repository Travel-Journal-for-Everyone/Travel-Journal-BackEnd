package com.traveljournal.domain.photo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.Image.entity.ImageInfo;
import com.traveljournal.domain.photo.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
	boolean existsByImageInfo(ImageInfo imageInfo);
}