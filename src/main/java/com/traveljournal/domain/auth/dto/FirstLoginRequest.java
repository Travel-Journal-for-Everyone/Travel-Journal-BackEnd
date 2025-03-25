package com.traveljournal.domain.auth.dto;

import com.traveljournal.domain.member.entity.AccountScope;

import io.swagger.v3.oas.annotations.media.Schema;

// MultipartFile은 JSON으로 직렬화할 수 없으므로 record 대신 클래스로 변경
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

	// Getter 메서드
	public String getNickname() {
		return nickname;
	}

	public AccountScope getAccountScope() {
		return accountScope;
	}

	// Setter 메서드
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setAccountScope(AccountScope accountScope) {
		this.accountScope = accountScope;
	}
}
