
package com.latteis.eumcoding.controller;

import com.fasterxml.jackson.core.JsonProcessingException;


import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.KakaoMemberService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/unauth")
public class KaKaoController {

    @Value("${kakao.clientId}")
    private String CLIENT_ID;

    @Value("${kakao.redirectUri}")
    private String REDIRECT_URI;

    private final KakaoMemberService kakaoMemberService;

    private final TokenProvider tokenProvider;

    // 카카오 로그인
/*
    @GetMapping("/auth/kakao/callback")
    public MemberDTO kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("콜백");
        return kakaoMemberService.kakaoLogin(code, response);
    }
*/
    @ApiOperation(value = "")
    @GetMapping("/kakao/accesstoken")
    public String getKakaoAccessToken(@RequestParam String code) {

        System.out.println("code : " + code);
        return kakaoMemberService.getKakaoAccessToken(code);
    }


    //사용자 정보
    @GetMapping("/user")
    @ApiOperation(value = "Get Kakao User Info", notes = "Get user info from Kakao using an Access Token.")
    public ResponseEntity<String> getKakaoUserInfo(@RequestParam("token") String token, HttpServletResponse response) {
        String userEmail = kakaoMemberService.createKakaoUser(token, response);
        return ResponseEntity.ok("User Info Fetched Successfully.");
    }


    @PostMapping("/createUser")
    @ApiOperation("카카오 계정과 일반 계정 연동")
    public ResponseEntity<String> createKakaoUser(@RequestParam String token, HttpServletResponse response) {
        String userEmail = kakaoMemberService.createKakaoUser(token, response);
        return ResponseEntity.ok("카카오 계정과 일반 계정이 연동되었습니다.");
    }


    // 카카오 로그인
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        System.out.println("콜백");
        String jwtToken = kakaoMemberService.kakaoLogin(code, response);
        System.out.println("jwt :" + jwtToken);
        return ResponseEntity.ok(jwtToken); // JWT 토큰을 반환합니다.
    }





}

