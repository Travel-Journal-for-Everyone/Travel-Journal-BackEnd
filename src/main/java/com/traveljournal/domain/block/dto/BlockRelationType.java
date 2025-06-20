package com.traveljournal.domain.block.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public enum BlockRelationType {
	@Schema(description = "차단 아님")
	NONE,
	@Schema(description = "내가 차단")
	BLOCKED_BY_ME,
	@Schema(description = "상대가 나를 차단")
	BLOCKED_ME,
	@Schema(description = "상호 차단")
	MUTUAL_BLOCK
}
