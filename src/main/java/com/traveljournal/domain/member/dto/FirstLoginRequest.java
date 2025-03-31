package com.traveljournal.domain.member.dto;

import com.traveljournal.domain.member.entity.AccountScope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

// MultipartFile은 JSON으로 직렬화할 수 없으므로 record 대신 클래스로 변경
@Getter
@Setter
public class FirstLoginRequest {
	@Schema(description = "사용자 닉네임", example = "도요새")
	private String nickname;

	@Schema(description = "계정 공개 범위")
	private AccountScope accountScope;

	// 기본 생성자
	public FirstLoginRequest() {}

	// 모든 필드를 포함한 생성자
	public FirstLoginRequest(String nickname, AccountScope accountScope) {
		this.nickname = nickname;
		this.accountScope = accountScope;
	}
}
