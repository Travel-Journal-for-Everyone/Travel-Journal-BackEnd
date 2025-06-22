package com.traveljournal.domain.statistics.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class MemberRegionStatisticsId implements Serializable {
	private Long memberId;
	private String regionGroup;

	public MemberRegionStatisticsId(Long memberId, String regionGroup) {
		this.memberId = memberId;
		this.regionGroup = regionGroup;
	}
}