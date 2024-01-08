package com.finfellows.domain.bookmark.presentation;

import com.finfellows.domain.bookmark.application.FinancialProductBookmarkServiceImpl;
import com.finfellows.domain.bookmark.application.PolicyInfoBookmarkServiceImpl;
import com.finfellows.domain.bookmark.application.PostBookmarkServiceImpl;
import com.finfellows.domain.post.domain.ContentType;
import com.finfellows.global.config.security.token.CurrentUser;
import com.finfellows.global.config.security.token.UserPrincipal;
import com.finfellows.global.payload.ErrorResponse;
import com.finfellows.global.payload.Message;
import com.finfellows.global.payload.ResponseCustom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Bookmark", description = "Bookmark API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {
    private final FinancialProductBookmarkServiceImpl financialProductBookmarkService;
    private final PolicyInfoBookmarkServiceImpl policyInfoBookmarkService;
    private final PostBookmarkServiceImpl postBookmarkService;


    @Operation(summary = "금융, 고마워 북마크", description = "금융, 고마워(정책)를 북마크한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 고마워.(정책) 즐겨찾기 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 고마워.(정책) 즐겨찾기 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/policy-info/{policy_info_id}")
    public ResponseCustom<?> bookmarkPolicyInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "금융, 고마워(정책) id를 입력해주세요.", required = true) @Valid @PathVariable("policy_info_id") Long policy_info_id
            ) {
        return ResponseCustom.OK(policyInfoBookmarkService.insert(userPrincipal, policy_info_id));
    }

    @Operation(summary = "금융, 고마워 북마크 삭제", description = "금융, 고마워(정책) 북마크를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 고마워.(정책) 즐겨찾기 삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 고마워.(정책) 즐겨찾기 삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping("/policy-info/{policy_info_id}")
    public ResponseCustom<?> deleteBookmarkPolicyInfo(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "금융, 고마워(정책) id를 입력해주세요.", required = true) @Valid @PathVariable("policy_info_id") Long policy_info_id
    ) {
        return ResponseCustom.OK(policyInfoBookmarkService.delete(userPrincipal, policy_info_id));
    }

    @Operation(summary = "금융, 배우자 북마크", description = "금융, 배우자(교육, 뉴스)를 북마크한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 배우자.(교육, 뉴스) 즐겨찾기 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 배우자.(교육, 뉴스) 즐겨찾기 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/posts/{post_id}")
    public ResponseCustom<?> bookmarkPost(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "금융, 배우자(교육, 뉴스) id를 입력해주세요.", required = true) @Valid @PathVariable("post_id") Long post_id,
            @Parameter(description = "ContentType을 Params로 입력해주세요. 예시) contentType=EDU_CONTENT", required = true) @RequestParam("contentType") ContentType contentType
    ) {
        return ResponseCustom.OK(postBookmarkService.insert(userPrincipal, post_id, contentType));
    }

    @Operation(summary = "금융, 배우자 북마크 삭제", description = "금융, 배우자(교육, 뉴스) 북마크를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 배우자(교육, 뉴스). 즐겨찾기 삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 배우자(교육, 뉴스). 즐겨찾기 삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping("/posts/{post_id}")
    public ResponseCustom<?> deleteBookmarkPost(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "금융, 배우자(교육, 뉴스) id를 입력해주세요.", required = true) @Valid @PathVariable("post_id") Long post_id
    ) {
        return ResponseCustom.OK(postBookmarkService.delete(userPrincipal, post_id));
    }

    @Operation(summary = "금융, 뭐하지 북마크", description = "금융, 뭐하지(금융 상품)를 북마크한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 뭐하지(금융 상품) 즐겨찾기 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 뭐하지(금융 상품) 즐겨찾기 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/financial-products/{financial_product_id}")
    public ResponseCustom<?> bookmarkFinancialProduct(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "금융, 뭐하지(금융 상품) id를 입력해주세요.", required = true) @Valid @PathVariable("financial_product_id") Long financial_product_id
    ) {
        return ResponseCustom.OK(financialProductBookmarkService.insert(userPrincipal, financial_product_id));
    }

    @Operation(summary = "금융, 뭐하지 북마크 삭제", description = "금융, 뭐하지(금융 상품) 북마크를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 뭐하지(금융 상품). 즐겨찾기 삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 뭐하지(금융 상품). 즐겨찾기 삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping("/financial-products/{financial_product_id}")
    public ResponseCustom<?> deleteBookmarkFinancialProduct(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "금융, 뭐하지(금융 상품) id를 입력해주세요.", required = true) @Valid @PathVariable("financial_product_id") Long financial_product_id
    ) {
        return ResponseCustom.OK(financialProductBookmarkService.delete(userPrincipal, financial_product_id));
    }



}
