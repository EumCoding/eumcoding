package com.latteis.eumcoding.service;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.model.KakaoInfo;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.KakaoInfoRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.security.JwtAuthenticationFilter;
import com.latteis.eumcoding.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoMemberService {

    @Value("${kakao.clientId}")
    private String CLIENT_ID;

    @Value("${kakao.redirectUri}")
    private String REDIRECT_URI;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MemberRepository memberRepository;
    private final KakaoInfoRepository kakaoInfoRepository;
    private final UnauthMemberService unauthMemberService;

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;



    public String getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + CLIENT_ID); // properties-asdf에다 작성함
            sb.append("&redirect_uri=" + REDIRECT_URI); // properties-asdf에다 작성함
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    public String createKakaoUser(String token, HttpServletResponse response){

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String userEmail = null;

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String kakaoEmail = "";
            if(hasEmail){
                kakaoEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("email : " + kakaoEmail);

            //일반회원 가입 시 햇던 이메일과 카카오에 있는 이메일 비교
            Member member = memberRepository.findByEmail(kakaoEmail);

            //이미 연동된 카카오 계정인지 확인
            KakaoInfo existingKakaoInfo = kakaoInfoRepository.findByKakaoEmailAndEmail(kakaoEmail);
            if(existingKakaoInfo != null){
                System.out.println("이미 연동된 계정입니다.");
                return kakaoEmail;
            }


            if (member != null) {
                // 기존에 일반 회원가입한 이메일과 일치하면 연동 처리
                member.setEmail(kakaoEmail);
                memberRepository.save(member);

                userEmail = kakaoEmail;
                System.out.println("카카오 계정과 일반 계정이 연동되었습니다.");

                //카카오 정보 저장
                KakaoInfo kakaoInfo = new KakaoInfo();
                kakaoInfo.setEmail(member.getEmail());
                kakaoInfo.setKakaoEmail(kakaoEmail);
                kakaoInfo.setKakaoUserId(id);
                kakaoInfo.setKakaoAccessToken(token);
                kakaoInfo.setJoinDay(LocalDateTime.now());
                kakaoInfoRepository.save(kakaoInfo);


                // JWT 토큰 생성 및 클라이언트에게 전달
                MemberDTO memberDTO = new MemberDTO(member);
                String jwtToken = tokenProvider.create(memberDTO); // 일반 회원의 JWT 토큰 생성
                kakaoMemberAuthorizationInput(jwtToken, response); // 클라이언트에게 JWT 토큰 전달


            } else {
                System.out.println("해당 이메일과 일치하는 일반 회원 계정이 없습니다.");
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return userEmail;
    }



    // JWT 토큰을 response Header에 추가
    private void kakaoMemberAuthorizationInput(String jwtToken, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + jwtToken); // "Bearer " 추가
    }

    public boolean isLinkedAccount(String kakaoEmail){
        KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmailAndEmail(kakaoEmail);
        return kakaoInfo != null;
    }

    public String kakaoLogin(String code, HttpServletResponse response) {
        // Access Token 얻기
        String accessToken = getKakaoAccessToken(code);

        String userEmail = createKakaoUser(accessToken,response);

        // 이 부분에서 연동된 계정인지 체크
        if(!isLinkedAccount(userEmail)) {
            throw new RuntimeException("이메일이 일반 계정과 연동되지 않았습니다.");
        }

        // 연동된 계정이므로, 해당 계정을 위한 JWT 토큰을 생성하고 반환합니다.
        Member member = memberRepository.findByEmail(userEmail);
        MemberDTO memberDTO = new MemberDTO(member);
        String jwtToken = tokenProvider.create(memberDTO);
        return jwtToken;
    }

}


