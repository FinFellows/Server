package com.finfellows.domain.auth.presentation;

import com.finfellows.domain.auth.application.KakaoService;
import com.finfellows.domain.auth.dto.AuthRes;
import com.finfellows.domain.auth.dto.KakaoProfile;
import com.finfellows.domain.auth.dto.RefreshTokenReq;
import com.finfellows.domain.auth.dto.TokenMapping;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "Authorization", description = "Authorization API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KakaoService kakaoService;


//    @Operation(summary = "카카오 code 발급", description = "카카오 API 서버에 접근 권한을 인가하는 code를 발급받습니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "code 발급 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class))}),
//            @ApiResponse(responseCode = "400", description = "code 발급 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
//    })
//    @GetMapping(value = "/login")
//    public void socialLoginRedirect() throws IOException {
//        kakaoService.accessRequest();
//    }

    @Operation(summary = "유저 정보 확인", description = "현재 접속 중인 유저의 정보를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 확인 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = User.class) ) } ),
            @ApiResponse(responseCode = "400", description = "유저 확인 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @GetMapping
    public ResponseCustom<?> whoAmI(
            @Parameter(description = "AccessToken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return kakaoService.whoAmI(userPrincipal);
    }

    @Operation(summary = "카카오 로그인", description = "카카오 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class))}),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/kakao/sign-in")
    public ResponseCustom<?> kakaoCallback(
            @Parameter(description = "code를 입력해주세요.", required = true) @RequestParam("code") String code
    ) {
        String accessToken = kakaoService.getKakaoAccessToken(code);
        KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(accessToken);


        return ResponseCustom.OK(kakaoService.kakaoLogin(kakaoProfile));

    }


    @Operation(summary = "관리자 회원가입", description = "관리자 권한으로 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class))}),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping(value = "/admin/sign-in")
    public ResponseCustom<?> adminSignIn(
            @Parameter(description = "code를 입력해주세요.", required = true) @RequestParam("code") String code
            ) {
        String accessToken = kakaoService.getKakaoAccessToken(code);
        KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(accessToken);

        return ResponseCustom.OK(kakaoService.adminSignIn(kakaoProfile));
    }


    @Operation(summary = "유저 로그아웃", description = "유저 로그아웃을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "로그아웃 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping(value = "sign-out")
    public ResponseCustom<?> signOut(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "Schemas의 RefreshTokenRequest를 참고해주세요.") @Valid @RequestBody RefreshTokenReq tokenRefreshRequest
    ) {
        return ResponseCustom.OK(kakaoService.signOut(tokenRefreshRequest));
    }


    @Operation(summary = "회원 탈퇴", description = "현재 접속된 유저 계정을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @DeleteMapping
    public ResponseCustom<?> deleteAccount(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ){
        return ResponseCustom.OK(kakaoService.deleteAccount(userPrincipal));
    }

    @Operation(summary = "토큰 갱신", description = "토큰을 갱신을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = AuthRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "토큰 갱신 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @Parameter(description = "Schemas의 RefreshTokenReq를 참고해주세요.", required = true) @Valid @RequestBody RefreshTokenReq refreshTokenReq
    ) {
        return kakaoService.refresh(refreshTokenReq);
    }


}
