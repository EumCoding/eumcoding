package com.latteis.eumcoding.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.latteis.eumcoding.dto.MemberDTO;

import com.latteis.eumcoding.dto.SocialUserInfoDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.security.JwtAuthenticationFilter;
import com.latteis.eumcoding.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMemberService {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;



    public MemberDTO kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        MemberDTO kakaoUserInfo = getKakaoMemberInfo(accessToken);

        // 3. 카카오ID로 회원가입 처리
        //Member kakaoUser = memberRepository.findByEmail(kakaoUserInfo.getEmail());

        MemberDTO memberDTO = null;
        Member kakaoUser = registerKakaoMemberIfNeed(memberDTO);

        // 4. 강제 로그인 처리
        Authentication authentication = forceLogin(kakaoUser);

        // 5. response Header에 JWT 토큰 추가
        kakaoMemberAuthorizationInput(authentication, response);
        return kakaoUserInfo;
    }


    private String getAccessToken(String code) throws JsonProcessingException {
        RestTemplate rt = new RestTemplate();
        //HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type","application/x-www-form-urlencoded;charset=utf-8");

        //HTTP Body 생성
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","b2922d14efb0c57c0757b5c912b5b005");
        params.add("redirect_uri","http://localhost:8089/auth/kakao/callback");
        params.add("code",code);

        //HTTP 요청 보내기
        HttpEntity<MultiValueMap<String,String>> kakaoToeknRequest =
                new HttpEntity<>(params,headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoToeknRequest,
                String.class

        );

        // 제이슨을 ObjectMapper 라이브러리에 담는다.
    /*    ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }*/

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        return jsonNode.get("access_token").asText();



    }

    // 2. 토큰으로 카카오 API 호출
    private MemberDTO getKakaoMemberInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        int id = (int) jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String name = jsonNode.get("properties")
                .get("name").asText();

        return new MemberDTO(id, name, email);
    }


    // 3. 카카오ID로 회원가입 처리
    private Member registerKakaoMemberIfNeed (MemberDTO memberDTO) {
        // DB 에 중복된 email이 있는지 확인
        String kakaoEmail = memberDTO.getEmail();
        String nickname = memberDTO.getNickname();
        Member kakaoUser = memberRepository.findByEmail(kakaoEmail);

        if (kakaoUser == null) {
            // 회원가입
            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            String profile = "";

            kakaoUser = Member.builder()
                    .email(kakaoEmail)
                    .nickname(nickname)
                    .profile(profile)
                    .password(encodedPassword)
                    .role(1)
                    .build();

            memberRepository.save(kakaoUser);

        }
        return kakaoUser;
    }

   // public UsernamePasswordAuthenticationToken(Object principal, Object credentials,  Collection<? extends GrantedAuthority> authorities)


    // 4. 강제 로그인 처리
    private Authentication forceLogin(Member kakaoUser) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(kakaoUser.getRole(), kakaoUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }


    // TokenProvider 클래스의 generateJwtToken() 메소드를 사용하여 토큰을 생성하고 헤더에 추가
    private void kakaoMemberAuthorizationInput(Authentication authentication, HttpServletResponse response) {
        // response header에 token 추가
        Member kakaoMember = (Member) authentication.getPrincipal(); // 사용자 정보 가져오기
        String token = tokenProvider.create(new MemberDTO(kakaoMember)); // TokenProvider 클래스의 create() 메소드 사용
        response.addHeader("Authorization", "Bearer " + token); // "Bearer " 추가
    }






}
