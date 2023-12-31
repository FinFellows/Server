package com.finfellows.domain.auth.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finfellows.domain.auth.domain.Token;
import com.finfellows.domain.auth.domain.repository.TokenRepository;
import com.finfellows.domain.auth.dto.*;
import com.finfellows.domain.user.domain.Role;
import com.finfellows.domain.user.domain.User;
import com.finfellows.domain.user.domain.repository.UserRepository;
import com.finfellows.global.DefaultAssert;
import com.finfellows.global.config.security.OAuth2Config;
import com.finfellows.global.config.security.token.UserPrincipal;
import com.finfellows.global.error.DefaultAuthenticationException;
import com.finfellows.global.payload.ErrorCode;
import com.finfellows.global.payload.Message;
import com.finfellows.global.payload.ResponseCustom;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

    @Autowired
    private OAuth2Config oAuth2Config;

    private final AuthenticationManager authenticationManager;

    private final RestTemplate rt;
    private final ObjectMapper objectMapper;
    private final HttpServletResponse response;

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final CustomTokenProviderService customTokenProviderService;


    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String KAKAO_SNS_URL;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_SNS_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirect_Uri;

    @Transactional
    public String getKakaoAccessToken(String code) {

        String access_Token = "";
        String refresh_Token = "";

        // Post 요청 라이브러리
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // http 바디 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_SNS_CLIENT_ID);
        params.add("redirect_uri", redirect_Uri);
        params.add("code", code);

        // httpHeader와 httpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // 실제 요청 Http post 방식 그리고 response 변수에 응답 받는다
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();

        OAuthToken oauthToken = null;

        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        access_Token = oauthToken.getAccess_token();

        return access_Token;

    }

    @Transactional
    public void accessRequest() throws IOException {

        Map<String, Object> params = new HashMap<>();
//        params.put("scope", "email");
        params.put("response_type", "code");
        params.put("client_id", KAKAO_SNS_CLIENT_ID);
        params.put("redirect_uri", redirect_Uri);


        //parameter를 형식에 맞춰 구성해주는 함수
        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));
        String redirectURL = KAKAO_SNS_URL + "?" + parameterString;
        log.info("redirectURL = ", redirectURL);

        response.sendRedirect(redirectURL);
    }

    @Transactional
    public KakaoProfile getKakaoProfile(String accessToken) {
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + accessToken);
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest =
                new HttpEntity<>(headers2);

        ResponseEntity<String> response2 = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );


        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper.readValue(response2.getBody(), KakaoProfile.class);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return kakaoProfile;
    }


    @Transactional
    public AuthRes kakaoLogin(KakaoProfile kakaoProfile) {

        // 이미 DB에 회원 정보가 저장되어 있으면 로그인 시키고, 없다면 DB에 등록 후 로그인.

        Optional<User> byEmail = userRepository.findByEmail(kakaoProfile.getKakaoAccount().getEmail());
        if (!byEmail.isPresent()) {
            User user = User.builder()
                    .providerId(kakaoProfile.getId())
                    .email(kakaoProfile.getKakaoAccount().getEmail())
                    .name(kakaoProfile.getKakaoAccount().getProfile().getNickname())
                    .role(Role.USER)
                    .build();

            User saveUser = userRepository.save(user);


        }


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        kakaoProfile.getKakaoAccount().getEmail(),
                        kakaoProfile.getId() //providerId랑 같다.
                )
        );



        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);


        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .email(tokenMapping.getEmail())
                .build();

        tokenRepository.save(token);

        Token savedToken = tokenRepository.save(token);


        return AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .role(Role.USER)
                .build();
    }

    @Transactional
    public Message signOut(final RefreshTokenReq tokenRefreshRequest) {
        Token token = tokenRepository.findByRefreshToken(tokenRefreshRequest.getRefreshToken())
                .orElseThrow(() -> new DefaultAuthenticationException(ErrorCode.INVALID_AUTHENTICATION));
        tokenRepository.delete(token);

        return Message.builder()
                .message("로그아웃 하였습니다.")
                .build();
    }

  
    @Transactional
    public Message deleteAccount(UserPrincipal userPrincipal) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isTrue(user.isPresent(), "유저가 올바르지 않습니다.");

        Optional<Token> token = tokenRepository.findByEmail(userPrincipal.getEmail());
        DefaultAssert.isTrue(token.isPresent(), "토큰이 유효하지 않습니다.");

        userRepository.delete(user.get());
        tokenRepository.delete(token.get());


        return Message.builder()
                .message("회원 탈퇴 하였습니다.")
                .build();
    }

    @Transactional
    public AuthRes adminSignIn(KakaoProfile kakaoProfile) {
        Optional<User> byEmail = userRepository.findByEmail(kakaoProfile.getKakaoAccount().getEmail());
        if (!byEmail.isPresent()) {
            User user = User.builder()
                    .providerId(kakaoProfile.getId())
                    .email(kakaoProfile.getKakaoAccount().getEmail())
                    .name(kakaoProfile.getKakaoAccount().getProfile().getNickname())
                    .role(Role.ADMIN)
                    .build();

            User saveUser = userRepository.save(user);


        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        kakaoProfile.getKakaoAccount().getEmail(),
                        kakaoProfile.getId() //providerId랑 같다.
                )
        );



        TokenMapping tokenMapping = customTokenProviderService.createToken(authentication);


        Token token = Token.builder()
                .refreshToken(tokenMapping.getRefreshToken())
                .email(tokenMapping.getEmail())
                .build();

        tokenRepository.save(token);

        Token savedToken = tokenRepository.save(token);


        return AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .build();

    }

    public ResponseEntity<?> refresh(RefreshTokenReq refreshTokenReq) {
        //1차 검증
        boolean checkValid = valid(refreshTokenReq.getRefreshToken());
        DefaultAssert.isAuthentication(checkValid);

        Optional<Token> token = tokenRepository.findByRefreshToken(refreshTokenReq.getRefreshToken());
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getEmail());

        //4. refresh token 정보 값을 업데이트 한다.
        //시간 유효성 확인
        TokenMapping tokenMapping;

        Long expirationTime = customTokenProviderService.getExpiration(refreshTokenReq.getRefreshToken());
        if(expirationTime > 0){
            tokenMapping = customTokenProviderService.refreshToken(authentication, token.get().getRefreshToken());
        }else{
            tokenMapping = customTokenProviderService.createToken(authentication);
        }

        Token updateToken = token.get().updateRefreshToken(tokenMapping.getRefreshToken());
        tokenRepository.save(updateToken);

        AuthRes authResponse = AuthRes.builder()
                .accessToken(tokenMapping.getAccessToken())
                .refreshToken(updateToken.getRefreshToken())
                .role(Role.ADMIN)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    private boolean valid(String refreshToken) {

        // 1. 토큰 형식 물리적 검증
        boolean validateCheck = customTokenProviderService.validateToken(refreshToken);
        DefaultAssert.isTrue(validateCheck, "Token 검증에 실패하였습니다.");

        // 2. refresh token 값을 불러온다.
        Optional<Token> token = tokenRepository.findByRefreshToken(refreshToken);
        DefaultAssert.isTrue(token.isPresent(), "탈퇴 처리된 회원입니다.");

        // 3. email 값을 통해 인증값을 불러온다.
        Authentication authentication = customTokenProviderService.getAuthenticationByEmail(token.get().getEmail());
        DefaultAssert.isTrue(token.get().getEmail().equals(authentication.getName()), "사용자 인증에 실패했습니다.");

        return true;
    }

    public ResponseCustom<?> whoAmI(UserPrincipal userPrincipal) {
        Optional<User> user = userRepository.findById(userPrincipal.getId());
        DefaultAssert.isOptionalPresent(user);
        return ResponseCustom.OK(user);
    }
}
