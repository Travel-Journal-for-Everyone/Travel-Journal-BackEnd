package com.traveljournal.domain.auth.dto;

import com.traveljournal.domain.member.entity.AccountScope;

public record FirstLoginRequest(
	String nickname,
	AccountScope accountScope
) {
}
