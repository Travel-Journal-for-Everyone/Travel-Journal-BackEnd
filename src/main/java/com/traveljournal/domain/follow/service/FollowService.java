package com.traveljournal.domain.follow.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.follow.dto.FollowProfileResponse;
import com.traveljournal.domain.follow.dto.FollowRequestResponse;
import com.traveljournal.domain.follow.entity.Follow;
import com.traveljournal.domain.follow.entity.RequestStatus;
import com.traveljournal.domain.follow.repository.FollowRepository;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.statistics.service.MemberStatisticsService;
import com.traveljournal.global.exception.FollowAccountScopeException;
import com.traveljournal.global.exception.FollowBadRequestException;
import com.traveljournal.global.exception.FollowNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final FollowRepository followRepository;
	private final MemberService memberService;
	private final MemberStatisticsService memberStatisticsService;

	private Member findMemberById(Long id) {
		return memberService.findById(id);
	}

	@Transactional
	public String follow(Long fromMemberId, Long toMemberId) {
		if (fromMemberId.equals(toMemberId)) {
			throw new FollowBadRequestException("자신을 팔로우 할 수 없습니다.");
		}

		if (followRepository.existsByFromMemberIdAndToMemberId(fromMemberId, toMemberId)) {
			throw new FollowBadRequestException("이미 팔로우한 사용자입니다.");
		}
		Member fromMember = findMemberById(fromMemberId);
		Member toMember = findMemberById(toMemberId);

		RequestStatus status;
		String message;

		switch (toMember.getAccountScope()) {
			case PUBLIC -> {
				status = RequestStatus.ACCEPTED;
				message = "팔로우 성공";
			}
			case FRIENDS, PRIVATE -> {
				status = RequestStatus.REQUESTED;
				message = "팔로우 요청 성공";
			}
			default -> throw new FollowAccountScopeException("알 수 없는 계정 범위입니다.");
		}

		Follow follow = Follow.builder()
			.fromMember(fromMember)
			.toMember(toMember)
			.requestStatus(status)
			.build();

		followRepository.save(follow);

		if (status == RequestStatus.ACCEPTED) {
			memberStatisticsService.increaseFollowerCount(toMemberId);
			memberStatisticsService.increaseFollowingCount(fromMemberId);
		}

		return message;
	}

	@Transactional
	public void unfollow(Long fromMemberId, Long toMemberId) {
		Follow follow = followRepository.findByFromMemberIdAndToMemberIdAndRequestStatus(
			fromMemberId, toMemberId, RequestStatus.ACCEPTED
		).orElseThrow(() -> new FollowNotFoundException("팔로우 관계가 존재하지 않거나, 아직 수락되지 않았습니다."));
		followRepository.deleteByFromMemberIdAndToMemberId(fromMemberId, toMemberId);
		memberStatisticsService.decreaseFollowerCount(toMemberId);
		memberStatisticsService.decreaseFollowingCount(fromMemberId);
	}

	// 내가 팔로우한 사람들
	@Transactional(readOnly = true)
	public Page<FollowProfileResponse> findFollowings(Long memberId, Pageable pageable) {
		Member member = findMemberById(memberId);
		return followRepository.findByFromMemberAndRequestStatus(member, RequestStatus.ACCEPTED, pageable)
			.map(follow -> FollowProfileResponse.of(follow.getToMember()));
	}

	// 나를 팔로우하는 사람들
	@Transactional(readOnly = true)
	public Page<FollowProfileResponse> findFollowers(Long memberId, Pageable pageable) {
		Member member = findMemberById(memberId);
		return followRepository.findByToMemberAndRequestStatus(member, RequestStatus.ACCEPTED, pageable)
			.map(follow -> FollowProfileResponse.of(follow.getFromMember()));
	}

	@Transactional(readOnly = true)
	public long getFollowerCount(Long memberId) {
		Member member = findMemberById(memberId);
		return followRepository.countByToMemberAndRequestStatus(member, RequestStatus.ACCEPTED);
	}

	@Transactional(readOnly = true)
	public long getFollowingCount(Long memberId) {
		Member member = findMemberById(memberId);
		return followRepository.countByFromMemberAndRequestStatus(member, RequestStatus.ACCEPTED);
	}

	@Transactional(readOnly = true)
	public boolean isFollowing(Long followerId, Long followingId) {
		return followRepository.existsByFromMemberIdAndToMemberIdAndRequestStatus(followerId, followingId,
			RequestStatus.ACCEPTED);
	}

	@Transactional
	public void acceptFollowRequest(Long memberId, Long followId) {
		Follow follow = followRepository.findByIdAndToMemberId(followId, memberId)
			.orElseThrow(() -> new FollowNotFoundException("팔로우 요청이 존재하지 않습니다."));
		if (follow.getRequestStatus() == RequestStatus.REQUESTED) {
			follow.accept();
			memberStatisticsService.increaseFollowerCount(follow.getToMember().getId());
			memberStatisticsService.increaseFollowingCount(follow.getFromMember().getId());
		}
	}

	@Transactional
	public void rejectFollowRequest(Long memberId, Long followId) {
		Follow follow = followRepository.findByIdAndToMemberId(followId, memberId)
			.orElseThrow(() -> new FollowNotFoundException("팔로우 요청이 존재하지 않습니다."));
		follow.reject();
	}

	@Transactional(readOnly = true)
	public Page<FollowRequestResponse> findFollowRequests(Long memberId, Pageable pageable) {
		return followRepository.findAllByToMemberIdAndRequestStatus(memberId, RequestStatus.REQUESTED, pageable)
			.map(FollowRequestResponse::of);
	}

	@Transactional(readOnly = true)
	public List<Long> getFollowingMemberIds(Long followerId) {
		return followRepository.findFollowedIdsByFollowerId(followerId);
	}
}
