package com.traveljournal.domain.block.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.block.dto.BlockRelationType;
import com.traveljournal.domain.block.dto.BlockResponse;
import com.traveljournal.domain.block.entity.Block;
import com.traveljournal.domain.block.repository.BlockRepository;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.global.exception.BlockBadRequestException;
import com.traveljournal.global.exception.ForbiddenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlockService {
	private final BlockRepository blockRepository;
	private final MemberService memberService;

	private Member findMemberById(Long id) {
		return memberService.findById(id);
	}

	@Transactional
	public void blockMember(Long blockerId, Long blockedId) {

		Member blocker = findMemberById(blockerId);
		Member blocked = findMemberById(blockedId);

		if (blockRepository.existsByBlockerAndBlocked(blocker, blocked)) {
			throw new BlockBadRequestException("이미 차단한 사용자입니다.");
		}

		Block block = Block.builder()
			.blocker(blocker)
			.blocked(blocked)
			.build();

		blockRepository.save(block);
	}

	public void unblockMember(Long blockerId, Long blockedId) {
		Member blocker = findMemberById(blockerId);
		Member blocked = findMemberById(blockedId);

		Block block = blockRepository.findByBlockerAndBlocked(blocker, blocked)
			.orElseThrow(() -> new BlockBadRequestException("차단 내역이 없습니다."));

		blockRepository.delete(block);
	}

	@Transactional(readOnly = true)
	public Page<BlockResponse> getBlockedMembers(Long blockerId, Pageable pageable) {
		Member blocker = findMemberById(blockerId);
		return blockRepository.findAllByBlocker(blocker, pageable)
			.map(block -> BlockResponse.of(block.getBlocked()));
	}

	@Transactional(readOnly = true)
	public boolean isBlocked(Member viewer, Member target) {
		return blockRepository.existsByBlockerAndBlocked(viewer, target) ||
			blockRepository.existsByBlockerAndBlocked(target, viewer);
	}

	@Transactional(readOnly = true)
	public BlockRelationType getBlockRelation(Long viewerId, Long memberId) {
		boolean blockedByMe = blockRepository.existsByBlockerIdAndBlockedId(viewerId, memberId);
		boolean blockedMe = blockRepository.existsByBlockerIdAndBlockedId(memberId, viewerId);

		if (blockedByMe && blockedMe) {
			return BlockRelationType.MUTUAL_BLOCK;
		} else if (blockedByMe) {
			return BlockRelationType.BLOCKED_BY_ME;
		} else if (blockedMe) {
			return BlockRelationType.BLOCKED_ME;
		} else {
			return BlockRelationType.NONE;
		}
	}

	@Transactional(readOnly = true)
	public List<Long> getBlockedMemberIds(Long viewerId) {

		List<Long> blockedByMe = blockRepository.findBlockedMemberIdsByBlockerId(viewerId);
		List<Long> blockedMe = blockRepository.findBlockerIdsByBlockedId(viewerId);
		return Stream.concat(blockedByMe.stream(), blockedMe.stream())
			.distinct()
			.toList();
	}

	@Transactional(readOnly = true)
	public void validateNotBlocked(Long viewerId, Long memberId) {
		BlockRelationType relation = getBlockRelation(viewerId, memberId);
		if (relation != BlockRelationType.NONE) {
			throw new ForbiddenException("차단된 회원입니다.");
		}
	}
}