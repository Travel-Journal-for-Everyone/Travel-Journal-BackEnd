package com.traveljournal.domain.auth.dto.apple;

public record AppleIdTokenInfo(
	String sub,
	String email,
	boolean emailVerified
) {}