package com.traveljournal.domain.journal.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "journal_day_spot")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JournalDaySpot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int spotOrder;

	@Column(length = 255)
	private String spotName;

	private Double latitude;
	private Double longitude;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "journal_day_id")
	private JournalDay journalDay;

}
