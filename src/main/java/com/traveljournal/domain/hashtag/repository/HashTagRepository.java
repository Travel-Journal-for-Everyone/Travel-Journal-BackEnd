package com.traveljournal.domain.hashtag.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.hashtag.entity.HashTag;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
	Optional<HashTag> findByTagName(String tagName);
}
