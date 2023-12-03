package com.latteis.eumcoding.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.latteis.eumcoding.config.CrypoUtils;
import com.latteis.eumcoding.dto.KakaoInfoDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.model.*;
import com.latteis.eumcoding.persistence.EmailKakaoNumberRepository;
import com.latteis.eumcoding.persistence.EmailNumberRepository;
import com.latteis.eumcoding.persistence.KakaoInfoRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.security.JwtAuthenticationFilter;
import com.latteis.eumcoding.security.TokenProvider;
import io.swagger.models.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.K;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMemberService {

    @Value("${kakao.clientId}")
    private String CLIENT_ID;

    @Value("${kakao.redirectUri}")
    private String REDIRECT_URI;

    @Value("${kakao.loginUri}")
    private String LOGIN_URI;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MemberRepository memberRepository;
    private final KakaoInfoRepository kakaoInfoRepository;
    private final EmailNumberRepository emailNumberRepository;
    private final EmailNumberService emailNumberService;
    private final EmailKakaoNumberService emailKakaoNumberService;
    private final EmailKakaoNumberRepository emailKakaoNumberRepository;

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 카카오 로그인 access_token 리턴
    public KakaoInfoDTO getAccessToken(String code){

        String tokenUri = "https://kauth.kakao.com/oauth/token";
        String access_Token = "";
        String refresh_Token = "";
        LocalDateTime access_Token_expire_in = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.set("grant_type", "authorization_code");
        parameters.set("client_id", CLIENT_ID);
        parameters.set("redirect_uri", REDIRECT_URI);
        parameters.set("code", code);

        HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, restRequest, String.class);

        KakaoInfoDTO kakaoInfoDTO = extractAccessToken(response.getBody());

        return kakaoInfoDTO;
    }

    // 카카오 로그인 access_token 리턴 only Login
    public KakaoInfoDTO getAccessTokenForLogin(String code){

        String tokenUri = "https://kauth.kakao.com/oauth/token";
        String access_Token = "";
        String refresh_Token = "";
        LocalDateTime access_Token_expire_in = null;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.set("grant_type", "authorization_code");
        parameters.set("client_id", CLIENT_ID);
        parameters.set("redirect_uri", LOGIN_URI);
        parameters.set("code", code);

        HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, restRequest, String.class);

        KakaoInfoDTO kakaoInfoDTO = extractAccessToken(response.getBody());

        return kakaoInfoDTO;
    }

    private KakaoInfoDTO extractAccessToken(String responseBody){
        JSONObject jsonObject = new JSONObject(responseBody);
        String accessToken = jsonObject.getString("access_token");
        String refreshToken = jsonObject.has("refresh_token") ? jsonObject.getString("refresh_token") : null; ;
        Long expiresIn = jsonObject.getLong("expires_in"); // JSON에서 정수 값으로 추출
        LocalDateTime expired = LocalDateTime.now().plusSeconds(expiresIn); // LocalDateTime에 초를 더함

        KakaoInfoDTO kakaoInfoDTO = new KakaoInfoDTO();
        kakaoInfoDTO.setAccessToken(accessToken);
        kakaoInfoDTO.setRefreshToken(refreshToken);
        kakaoInfoDTO.setExpiresIn(expired);


        return kakaoInfoDTO;
    }

    //연동을 하기 위해 일반 계정 로그인을 해야 함
    //메일보내는 메서드
    public String createKakaoAccountLinks(String code, Integer memberId) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        ObjectMapper mapper = new ObjectMapper();
        KakaoInfoDTO kakaoInfoDTO = getAccessToken(code);
        String token = kakaoInfoDTO.getAccessToken();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // HTTP 요청 보내기
            HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
            RestTemplate rt = new RestTemplate();
            ResponseEntity<String> response = rt.postForEntity(
                    reqURL,
                    kakaoUserInfoRequest,
                    String.class
            );

            String responseBody = response.getBody();
            JsonNode jsonNode = mapper.readTree(responseBody); //readTree 를 사용해서 JSON문자열을 JsonNode객체로 변환
            String id = jsonNode.get("id").asText();
            String kakaoEmail = jsonNode.get("kakao_account").get("email").asText();

            if(kakaoEmail == null){
                throw new IllegalArgumentException("해당 카카오 이메일이 없습니다.");
            }

            // 일반 계정의 이메일 주소를 얻습니다. (DB에서 사용자 정보를 가져와서)
            Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("멤버를 찾을 수 없습니다."));
            String memberEmail = member.getEmail();
            //token정보 암호화해서 DB에 저장
            String encryptedToken = CrypoUtils.encrypt(token);


            // 카카오 정보 저장
            KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoUserId(id);
            if(kakaoInfo == null){
                kakaoInfo = new KakaoInfo();
            }else if (kakaoInfo.getAgree() == 1) {
                // 이미 인증된 계정인 경우
                return "ALREADY_LINKED.";  //
            }


            kakaoInfo.setKakaoEmail(kakaoEmail);
            kakaoInfo.setKakaoUserId(id);
            kakaoInfo.setKakaoAccessToken(encryptedToken);
            kakaoInfo.setJoinDay(LocalDateTime.now());
            kakaoInfo.setMember(member);
            kakaoInfo.setRefreshToken(kakaoInfoDTO.getRefreshToken());
            kakaoInfo.setAccessTokenExpires(kakaoInfoDTO.getExpiresIn());
            kakaoInfo.setAgree(1);

            // KakaoInfo를 DB에 저장
            kakaoInfoRepository.save(kakaoInfo);
            //emailKakaoNumberService.sendVerificationNumber(memberId, memberEmail);
            return "SUCCESS";

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }

        return "ERROR";
    }


    // accessToken 만료될 경우 토큰을 새로고침하고 결과를 반환
    public KakaoInfoDTO refreshKakaoAccessTokens(String refreshToken) {
        String reqURL = "https://kauth.kakao.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("client_id", CLIENT_ID);
        parameters.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, Object>> restRequest = new HttpEntity<>(parameters, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(reqURL, restRequest, String.class);
            // 성공적인 응답을 확인합니다.
            if (response.getStatusCode().is2xxSuccessful()) {
                return extractAccessToken(response.getBody());
            } else {
                throw new IllegalStateException("리플래쉬 토큰 실패");
            }
        } catch (HttpClientErrorException e) {
            throw new IllegalStateException("리플래쉬 토큰 실패", e);
        }
    }

    // accessToken이 만료된 경우 새로운 accessToken 발급.
    @Transactional
    public String handleExpiredAccessTokens(String refreshToken) {
        KakaoInfo kakaoInfo = kakaoInfoRepository.findByRefreshToken(refreshToken);
        if (kakaoInfo == null) {
            throw new IllegalStateException("KakaoInfo에 대한 정보가 없습니다.");
        }

        KakaoInfoDTO newTokenInfo = refreshKakaoAccessTokens(refreshToken);
        if (newTokenInfo == null || newTokenInfo.getAccessToken() == null) {
            throw new IllegalStateException("새 액세스 토큰을 얻지 못했습니다.");
        }

        // KakaoInfo 객체에 새로운 토큰 정보를 업데이트합니다.
        kakaoInfo.setKakaoAccessToken(newTokenInfo.getAccessToken());
        kakaoInfo.setAccessTokenExpires(LocalDateTime.now().plusDays(1));
        kakaoInfoRepository.save(kakaoInfo);

        return newTokenInfo.getAccessToken();
    }

    private String getKakaoEmailFromTokens(String accessToken) {
        try {
            log.info("들어온 토큰: " + accessToken);
            String reqURL = "https://kapi.kakao.com/v2/user/me";
            ObjectMapper mapper = new ObjectMapper();

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // HTTP 요청 보내기
            HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
            RestTemplate rt = new RestTemplate();
            ResponseEntity<String> response = rt.postForEntity(reqURL, kakaoUserInfoRequest, String.class);


            String responseBody = response.getBody();
            JsonNode jsonNode = mapper.readTree(responseBody);
            String kakaoEmail = jsonNode.get("kakao_account").get("email").asText();
            log.info("카카오 이메일: " + kakaoEmail);

            if (kakaoEmail == null) {
                throw new IllegalArgumentException("해당 카카오 이메일이 없습니다.");
            }

            KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
            if(kakaoInfo == null){
                throw new IllegalArgumentException("카카오 이메일 검색결과가 null.");
            }
            if (LocalDateTime.now().isAfter(kakaoInfo.getAccessTokenExpires())) {
                String refreshToken = kakaoInfo.getRefreshToken();
                accessToken = handleExpiredAccessTokens(refreshToken);
                return getKakaoEmailFromTokens(accessToken);
            }


            return kakaoEmail;
        } catch (Exception e) {
            e.printStackTrace();
            // 다른 오류 처리
            throw new RuntimeException("API 요청 중 오류 발생", e);
        }
    }


    public MemberDTO kakaoLogin(String code, HttpServletResponse response) {
        try {
            KakaoInfoDTO kakaoInfoDTO = getAccessTokenForLogin(code);
            String accessToken = kakaoInfoDTO.getAccessToken();
            String kakaoEmail = getKakaoEmailFromTokens(accessToken);

            KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
            if (kakaoInfo == null) {
                throw new RuntimeException("해당 카카오 계정을 찾을 수 없습니다.");
            }

            // JWT 토큰 생성 및 반환
            Optional<Member> optionalMember = memberRepository.findById(kakaoInfo.getMember().getId());
            Member member = optionalMember.get();
            if (member == null) {
                throw new RuntimeException("해당 이메일을 가진 일반 계정을 찾을 수 없습니다.");
            }

            MemberDTO memberDTO = new MemberDTO(member);
            String jwtToken = tokenProvider.create(memberDTO);
            response.addHeader("Authorization", "Bearer " + jwtToken);

            MemberDTO responseMemberDTO = new MemberDTO(member);

            responseMemberDTO.setToken(jwtToken);
            // role
            responseMemberDTO.setRole(member.getRole());
            // memberId
            responseMemberDTO.setId(member.getId());

            return responseMemberDTO;

        } catch (AccessTokenExpiredException e) {
            throw new AccessTokenExpiredException("액세스 토큰 만료");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("로그인 중 오류 발생: " + e.getMessage(), e);
            throw new RuntimeException("로그인 중 오류 발생", e);
        }
    }





    public void verifyNumber(int verificationNumber, int memberId) {
        Optional<EmailKakaoNumber> emailNumberOpt = emailKakaoNumberRepository.findByVerificationNumberAndMemberEmail(verificationNumber,memberId);
        EmailKakaoNumber emailKakaoNumber = emailNumberOpt.orElseThrow(() ->{
            log.info("잘못된 인증 번호입니다.");
            return new IllegalArgumentException("잘못된 인증 번호입니다.");
        });

        if (emailKakaoNumber == null || emailKakaoNumber.getExpired() == 1 ) {
            throw new IllegalArgumentException("잘못된 인증 번호이거나 만료된 번호입니다.");
        }

        Optional<KakaoInfo> existing = kakaoInfoRepository.findByMemberId(memberId);
        if(existing.isPresent()){
            KakaoInfo kakaoInfo = existing.get();
            kakaoInfo.setAgree(1); // agree를 1로 설정,일반계정이랑 연동

            kakaoInfoRepository.save(kakaoInfo);  // KakaoInfo 업데이트
        } else {
            log.error("KakaoInfo를 찾을 수 없습니다.");
            throw new IllegalArgumentException("KakaoInfo를 찾을 수 없습니다.");
        }

        emailKakaoNumber.setNumberToUsed();
        emailKakaoNumberRepository.save(emailKakaoNumber);
    }


    public class AccessTokenExpiredException extends RuntimeException {
        public AccessTokenExpiredException(String message) {
            super(message);
        }
    }

}


