package com.traveljournal.domain.hashtag.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.hashtag.repository.HashTagRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashTagService {
	private final HashTagRepository hashTagRepository;

	public List<HashTag> getOrCreateHashTags(List<String> tagNames) {
		List<HashTag> tags = new ArrayList<>();
		for (String tagName : tagNames) {
			HashTag tag = hashTagRepository.findByTagName(tagName)
				.orElseGet(() -> hashTagRepository.save(HashTag.of(tagName)));
			tags.add(tag);
		}
		return tags;
	}
}
