package com.traveljournal.domain.statistics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberStatistics {

	@Id
	private Long memberId;

	@Column(nullable = false)
	private Long travelDiaryCount;

	@Column(nullable = false)
	private Long placesCount;

	@Column(nullable = false)
	private Long followerCount;

	@Column(nullable = false)
	private Long followingCount;

	// 도메인 메서드로만 상태 변경
	public void increaseTravelDiaryCount() {
		this.travelDiaryCount++;
	}

	public void decreaseTravelDiaryCount() {
		this.travelDiaryCount = Math.max(0, this.travelDiaryCount - 1);
	}
}
