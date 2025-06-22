package com.traveljournal.domain.statistics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_region_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberRegionStatistics {

	@EmbeddedId
	private MemberRegionStatisticsId id;

	@Column(nullable = false)
	private Long travelDiaryCount;

	@Column(nullable = false)
	private Long placesCount;

	public void increaseTravelDiaryCount() {
		this.travelDiaryCount++;
	}
	public void increasePlacesCount() {
		this.placesCount++;
	}

}