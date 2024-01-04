package com.finfellows.domain.product.presentation;

import com.finfellows.domain.product.application.FinancialProductServiceImpl;
import com.finfellows.domain.product.dto.condition.FinancialProductSearchCondition;
import com.finfellows.domain.product.dto.response.DepositDetailRes;
import com.finfellows.domain.product.dto.response.SavingDetailRes;
import com.finfellows.domain.product.dto.response.SearchFinancialProductRes;
import com.finfellows.global.config.security.token.CurrentUser;
import com.finfellows.global.config.security.token.UserPrincipal;
import com.finfellows.global.payload.ErrorResponse;
import com.finfellows.global.payload.PagedResponse;
import com.finfellows.global.payload.ResponseCustom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "FinancialProducts", description = "FinancialProducts API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/financial-products")
public class FinancialProductController {

    private final FinancialProductServiceImpl financialProductServiceImpl;

    @Operation(summary = "예금 정보 조회", description = "예금 정보를 조건에 따라 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예금 정보 조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SearchFinancialProductRes.class)))}),
            @ApiResponse(responseCode = "400", description = "예금 정보 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/deposit")
    public ResponseCustom<PagedResponse<SearchFinancialProductRes>> findDepositProducts(
            @Parameter(description = "AccessToken 을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @ModelAttribute FinancialProductSearchCondition financialProductSearchCondition,
            Pageable pageable
    ) {
        return ResponseCustom.OK(financialProductServiceImpl.findDepositProducts(userPrincipal, financialProductSearchCondition, pageable));
    }

    @Operation(summary = "적금 정보 조회", description = "적금 정보를 조건에 따라 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "적금 정보 조회 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SearchFinancialProductRes.class)))}),
            @ApiResponse(responseCode = "400", description = "적금 정보 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/saving")
    public ResponseCustom<PagedResponse<SearchFinancialProductRes>> findSavingProducts(
            @Parameter(description = "AccessToken 을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @ModelAttribute FinancialProductSearchCondition financialProductSearchCondition,
            Pageable pageable
    ) {
        return ResponseCustom.OK(financialProductServiceImpl.findSavingProducts(userPrincipal, financialProductSearchCondition, pageable));
    }

    @Operation(summary = "예금 상세 정보 조회", description = "예금 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예금 상세 정보 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DepositDetailRes.class))}),
            @ApiResponse(responseCode = "400", description = "예금 상세 정보 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/deposit/{deposit-id}")
    public ResponseCustom<DepositDetailRes> getDepositDetail(
            @Parameter(description = "AccessToken 을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @PathVariable(name = "deposit-id") Long depositId
    ) {
        return ResponseCustom.OK(financialProductServiceImpl.getDepositDetail(userPrincipal, depositId));
    }

    @Operation(summary = "적금 상세 정보 조회", description = "적금 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "적금 상세 정보 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SavingDetailRes.class))}),
            @ApiResponse(responseCode = "400", description = "적금 상세 정보 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/saving/{saving-id}")
    public ResponseCustom<SavingDetailRes> getSavingDetail(
            @Parameter(description = "AccessToken 을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @PathVariable(name = "saving-id") Long savingId
    ) {
        return ResponseCustom.OK(financialProductServiceImpl.getSavingDetail(userPrincipal, savingId));
    }

}