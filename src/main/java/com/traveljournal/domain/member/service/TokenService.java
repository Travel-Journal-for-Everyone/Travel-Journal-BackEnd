package com.traveljournal.domain.member.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.member.dto.MemberTokenResponse;
import com.traveljournal.domain.member.dto.ReissueRequest;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.Token;
import com.traveljournal.domain.member.repository.TokenRepository;
import com.traveljournal.global.data.JwtValidateStatus;
import com.traveljournal.global.exception.UnauthorizedException;
import com.traveljournal.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

	private final TokenRepository tokenRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;

	/**
	 * JWT 토큰 생성
	 */
	public TokenInfo createTokens(String email, String deviceId) {
		// 장치 ID가 없으면 생성
		if (deviceId == null || deviceId.isBlank()) {
			deviceId = UUID.randomUUID().toString();
		}
		String accessToken = jwtTokenProvider.createAccessToken(email);
		String refreshToken = jwtTokenProvider.createRefreshToken(email);
		return TokenInfo.of(accessToken, refreshToken, deviceId);
	}

	/**
	 * 회원과 장치 ID로 토큰 저장 또는 업데이트
	 */
	@Transactional
	public void saveOrUpdateToken(Member member, String deviceId, String refreshToken) {
		// 기존 토큰이 있는지 확인
		Optional<Token> existingToken = tokenRepository.findByDeviceId(deviceId);

		if (existingToken.isPresent()) {
			// 기존 토큰 업데이트
			Token token = existingToken.get();
			token.updateRefreshToken(refreshToken);
		} else {
			// 새 토큰 생성
			Token token = Token.builder()
				.member(member)
				.refreshToken(refreshToken)
				.deviceId(deviceId)
				.build();
			tokenRepository.save(token);
		}
	}

	/**
	 * 장치 ID로 리프레시 토큰 조회
	 */
	@Transactional(readOnly = true)
	public Optional<String> getRefreshTokenByDeviceId(String deviceId) {
		return tokenRepository.findByDeviceId(deviceId)
			.map(Token::getRefreshToken);
	}

	/**
	 * 회원 ID와 장치 ID로 토큰 삭제 (로그아웃)
	 */
	@Transactional
	public void deleteToken(Long memberId, String deviceId) {
		tokenRepository.findByDeviceIdAndMemberId(deviceId, memberId)
			.ifPresent(tokenRepository::delete);
	}

	/**
	 * 토큰 재발급
	 * 1. 리프레시 토큰 검증
	 * 2. 장치 ID로 토큰 정보 조회
	 * 3. 저장된 리프레시 토큰과 요청된 리프레시 토큰 비교
	 * 4. 새 액세스 토큰 발급
	 */
	@Transactional
	public MemberTokenResponse refreshToken(ReissueRequest reissueRequest) {
		// 리프레시 토큰 검증
		JwtValidateStatus status = jwtTokenProvider.getTokenValidationStatus(reissueRequest.refreshToken());
		if (status != JwtValidateStatus.ACCEPTED) {
			throw new UnauthorizedException("리프레시 토큰이 유효하지 않습니다.");
		}

		// 장치 ID로 토큰 정보 조회
		String deviceId = reissueRequest.deviceId();
		String email = jwtTokenProvider.getEmail(reissueRequest.refreshToken());
		Member member = memberService.findByEmail(email)
			.orElseThrow(() -> new UnauthorizedException("존재하지 않는 회원입니다."));

		// 저장된 토큰 조회
		String savedRefreshToken = getRefreshTokenByDeviceId(deviceId)
			.orElseThrow(() -> new UnauthorizedException("토큰 정보가 존재하지 않습니다."));

		// 토큰 비교
		if (!savedRefreshToken.equals(reissueRequest.refreshToken())) {
			throw new UnauthorizedException("리프레시 토큰이 일치하지 않습니다.");
		}

		// 새 액세스 토큰 발급
		String newAccessToken = jwtTokenProvider.createAccessToken(email);

		return MemberTokenResponse.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.tokenInfo(
				TokenInfo.builder()
					.accessToken(newAccessToken)
					.refreshToken(reissueRequest.refreshToken())
					.deviceId(deviceId)
					.build()
			)
			.build();
	}
}
