package com.latteis.eumcoding.service;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.latteis.eumcoding.dto.KakaoInfoDTO;
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


    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    public KakaoInfoDTO getKakaoAccessToken (String code) {
        String access_Token = "";
        String refresh_Token = "";
        LocalDateTime access_Token_expire_in = null;
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


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
            access_Token_expire_in = LocalDateTime.now().plusSeconds(element.getAsJsonObject().get("expires_in").getAsLong());

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);


            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        KakaoInfoDTO kakaoDTO = new KakaoInfoDTO();
        kakaoDTO.setAccessToken(access_Token);
        kakaoDTO.setRefreshToken(refresh_Token);
        kakaoDTO.setExpiresIn(access_Token_expire_in);
        return kakaoDTO;
    }


    public String refreshKakaoAccessToken(String refreshToken) {
        String newAccessToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=refresh_token");
            sb.append("&client_id=" + CLIENT_ID);
            sb.append("&refresh_token=" + refreshToken);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            newAccessToken = element.getAsJsonObject().get("access_token").getAsString();

/*            // 갱신된 액세스 토큰 저장 로직 추가
            KakaoInfo kakaoInfo = kakaoInfoRepository.findByRefreshToken(refreshToken);
            if (kakaoInfo != null) {
                kakaoInfo.setKakaoAccessToken(newAccessToken);
                kakaoInfoRepository.save(kakaoInfo);
            }*/

            bw.close();
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return newAccessToken;
    }
    //연동을 하기 위해 일반 계정 로그인을 해야 함
    public String createKakaoUser(String code,Integer memberId, HttpServletResponse response){

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        KakaoInfoDTO kakaoInfoDTO = getKakaoAccessToken(code);
        String token = kakaoInfoDTO.getAccessToken();


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

            //id 타입변경해서 나중에 이거 수정할거임
            String id = String.valueOf(element.getAsJsonObject().get("id").getAsInt());
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();

            String kakaoEmail = "";

            if(hasEmail){
                kakaoEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            System.out.println("id : " + id);
            System.out.println("email : " + kakaoEmail);


            Member member = memberRepository.findById(memberId).orElse(null);
            if (member == null) {
                throw new RuntimeException("일반 계정 id가 없습니다.");
            }

            //이미 연동된 카카오 계정인지 확인
            KakaoInfo existingKakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
            if(existingKakaoInfo != null){
                System.out.println("이미 연동된 계정입니다.");
                return "ALREADY_LINKED";
            }
            else{
                System.out.println("값이 없습니다.");
            }
            if(!kakaoEmail.equals(member.getEmail())) {
                throw new RuntimeException("해당 이메일과 일치하는 회원 계정이 없습니다.");
            }

            //String encryptedToken = AESUtil.encrypt(token);
            // 카카오 정보 저장
            KakaoInfo kakaoInfo = new KakaoInfo();
            kakaoInfo.setKakaoEmail(kakaoEmail);
            kakaoInfo.setKakaoUserId(id);
            kakaoInfo.setKakaoAccessToken(token);
            kakaoInfo.setJoinDay(LocalDateTime.now());
            kakaoInfo.setAgree(1);
            kakaoInfo.setRefreshToken(kakaoInfoDTO.getRefreshToken());
            kakaoInfo.setAccessTokenExpires(kakaoInfoDTO.getExpiresIn());
            kakaoInfo.setMember(member);

            br.close();
            return "SUCCESS";
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();

        }
        return "ERROR";
    }


    //로그인
    public String LoginKakaoUser(String accessToken, HttpServletResponse response){

        String reqURL = "https://kapi.kakao.com/v2/user/me";
        String userEmail = null;

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            // 만료된 액세스 토큰으로 인한 오류 처리
            if (responseCode == 401) { // 401 Unauthorized
                String kakaoEmail = kakaoInfoRepository.findByKakaoAccessToken(accessToken).getKakaoEmail();
                KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);

                // 토큰 갱신
                String newAccessToken = refreshKakaoAccessToken(kakaoInfo.getRefreshToken());
                kakaoInfo.setKakaoAccessToken(newAccessToken); // 갱신된 토큰 저장
                kakaoInfoRepository.save(kakaoInfo);

                // 다시 API 호출
                return LoginKakaoUser(newAccessToken, response);
            }

            // InputStream을 얻는 부분을 수정
            InputStream inputStream;
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }


            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("결과 : " + result);
            //HttpURLConnection의 에러 스트림 처리
            if (responseCode == HttpURLConnection.HTTP_OK) { // 200
                System.out.println("성공 : " + result);
            } else {
                // 에러 응답 처리
                System.out.println("에러: " + result);
            }


            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);


            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String kakaoEmail = "";
            if(hasEmail){
                kakaoEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }


            //일반회원 가입 시 햇던 이메일과 카카오에 있는 이메일 비교
            Member member = memberRepository.findByEmail(kakaoEmail);

            //이미 연동된 카카오 계정인지 확인
            KakaoInfo existingKakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
            //agree가 1인경우
            if(existingKakaoInfo != null && existingKakaoInfo.getAgree() == 1) {
                System.out.println("이미 연동된 계정입니다.");
                userEmail.equals(kakaoEmail) ;

                // JWT 토큰 생성 및 클라이언트에게 전달
                MemberDTO memberDTO = new MemberDTO(member);
                String jwtToken = tokenProvider.create(memberDTO); // 일반 회원의 JWT 토큰 생성
                kakaoMemberAuthorizationInput(jwtToken, response); // 클라이언트에게 JWT 토큰 전달

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
        KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
        return kakaoInfo != null;
    }

    public String kakaoLogin(String code, HttpServletResponse response) {
        // 1. Access Token 얻기
        KakaoInfoDTO kakaoInfoDTO = getKakaoAccessToken(code);
        String token = kakaoInfoDTO.getAccessToken();
        System.out.println("accessToken: " + token);
        String kakaoEmail = "";

        try {
            // 2. 카카오 사용자 정보를 이용하여 로그인
            kakaoEmail = LoginKakaoUser(token, response);
            System.out.println("성공: " + kakaoEmail);
        } catch (AccessTokenExpiredException e) {
            // 토큰 만료에 대한 특별한 처리
            KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
            System.out.println("Token 만료: " + kakaoEmail);
            if (kakaoInfo == null) {
                throw new RuntimeException("해당 이메일을 가진 KakaoInfo를 찾을 수 없습니다.");
            }

            // 3. refreshToken을 이용해 새로운 accessToken 획득
            token = refreshKakaoAccessToken(kakaoInfo.getRefreshToken());

            // 4. 사용자의 액세스 토큰 정보 업데이트
            kakaoInfo.setKakaoAccessToken(token);
            kakaoInfoRepository.save(kakaoInfo);

            // 5. 다시 카카오 사용자 정보를 이용하여 로그인 시도
            kakaoEmail = LoginKakaoUser(token, response);
        } catch (Exception e) {
            // 그 외의 예외 처리
            System.out.println("로그인 중 오류 발생: " + e.getMessage());
            throw new RuntimeException("로그인 중 오류 발생", e);
        }

        // 6. 연동된 계정인지 체크
        if(!isLinkedAccount(kakaoEmail)) {
            throw new RuntimeException("이메일이 일반 계정과 연동되지 않았습니다.");
        }

        KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
        if (kakaoInfo == null) {
            throw new RuntimeException("해당 이메일을 가진 KakaoInfo를 찾을 수 없습니다.");
        }

        Member member = memberRepository.findByEmail(kakaoEmail);
        if (member == null) {
            throw new RuntimeException("해당 이메일을 가진 일반 계정을 찾을 수 없습니다.");
        }

        // 7. JWT 토큰 생성 및 반환
        MemberDTO memberDTO = new MemberDTO(member);
        String jwtToken = tokenProvider.create(memberDTO);
        response.addHeader("Authorization", "Bearer " + jwtToken); // "Bearer " 추가하여 클라이언트에게 JWT 토큰 전달

        return jwtToken;
    }



    public class AccessTokenExpiredException extends RuntimeException {
        public AccessTokenExpiredException(String message) {
            super(message);
        }
    }
}


