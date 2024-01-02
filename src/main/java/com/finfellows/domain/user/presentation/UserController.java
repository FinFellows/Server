package com.finfellows.domain.user.presentation;

import com.finfellows.domain.bookmark.application.EduContentBookmarkServiceImpl;
import com.finfellows.domain.bookmark.application.FinancialProductBookmarkServiceImpl;
import com.finfellows.domain.bookmark.application.PolicyInfoBookmarkServiceImpl;
import com.finfellows.domain.user.application.UserService;
import com.finfellows.domain.user.domain.User;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final EduContentBookmarkServiceImpl eduContentBookmarkService;
    private final FinancialProductBookmarkServiceImpl financialProductBookmarkService;
    private final PolicyInfoBookmarkServiceImpl policyInfoBookmarkService;


    @Operation(summary = "금융, 뭐하지 즐겨찾기 내역 조회", description = "금융, 뭐하지(금융 상품) 즐겨찾기 내역을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 뭐하지.(금융 상품) 즐겨찾기 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 뭐하지.(금융 상품) 즐겨찾기 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/financial-products")
    public ResponseCustom<?> getBookmarkedFinancialProducts(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return financialProductBookmarkService.findBookmarks(userPrincipal);
    }

    @Operation(summary = "금융, 배우자 즐겨찾기 내역 조회", description = "금융, 배우자(교육) 즐겨찾기 내역을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 배우자.(교육) 즐겨찾기 삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 배우자.(교육) 즐겨찾기 삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/eduContents")
    public ResponseCustom<?> getBookmarkedEduContents(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return eduContentBookmarkService.findBookmarks(userPrincipal);
    }

    @Operation(summary = "금융, 고마워 즐겨찾기 내역 조회", description = "금융, 고마워(정책) 즐겨찾기 내역을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "금융, 고마워.(정책) 즐겨찾기 삭제 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "금융, 고마워.(정책) 즐겨찾기 삭제 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping("/policy-infos")
    public ResponseCustom<?> getBookmarkedPolicyInfos(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return policyInfoBookmarkService.findBookmarks(userPrincipal);
    }


}
