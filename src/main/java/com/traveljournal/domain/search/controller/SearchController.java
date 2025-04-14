package com.traveljournal.domain.search.controller;

import com.traveljournal.domain.search.dto.MemberSearchResponse;
import com.traveljournal.domain.search.service.MemberSearchService;
import com.traveljournal.global.data.ApiResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Valid
@RequestMapping("/v1/search")
@Tag(name = "Search API", description = "사용자 검색")
public class SearchController {

    private final MemberSearchService memberSearchService;

    @GetMapping("/members")
    @Operation(
            summary = "사용자 검색",
            description = "사용자가 입력한 키워드로 다른 사용자를 검색합니다.",
            security = @SecurityRequirement(name = "bearer-key"))
    public ResponseEntity<?> searchMembers(
            @RequestParam @NotBlank(message = "검색어는 비어 있을 수 없습니다.") String keyword,
            @PageableDefault(sort = "nickname") Pageable pageable)
    {
/*
        // 키워드가 비어 있을 경우, 409 상태 코드로 실패 메시지 반환
        if (keyword == null || keyword.isEmpty()) {
            return ApiResponseHandler.onFailure("검색어는 비어 있을 수 없습니다.");
        }

 */
        Page<MemberSearchResponse> result = memberSearchService.searchMembers(keyword, pageable);

        return ApiResponseHandler.getObjectSuccess(result);
    }
}
