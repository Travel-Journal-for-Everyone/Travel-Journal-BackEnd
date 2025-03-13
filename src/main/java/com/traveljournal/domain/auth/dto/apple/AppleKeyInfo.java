package com.traveljournal.domain.auth.dto.apple;

public record AppleKeyInfo(
	String kty,
	String kid,
	String use,
	String alg,
	String n,
	String e
) {}