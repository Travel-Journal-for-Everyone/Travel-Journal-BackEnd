package com.traveljournal.domain.auth.dto;

import com.traveljournal.domain.member.entity.AccountScope;

import io.swagger.v3.oas.annotations.media.Schema;

public record FirstLoginRequest(
	@Schema(description = "사용자 닉네임", example = "도요새")
	String nickname,
	AccountScope accountScope
) {
}
