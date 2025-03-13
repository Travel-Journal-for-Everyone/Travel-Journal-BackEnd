package com.traveljournal.domain.auth.dto.apple;

import java.util.List;

public record ApplePublicKeyResponse(
	List<AppleKeyInfo> keys
) {}