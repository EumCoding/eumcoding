package com.latteis.eumcoding.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MemberRepository memberRepository;
    private final KakaoInfoRepository kakaoInfoRepository;
    private final EmailNumberRepository emailNumberRepository;
    private final EmailNumberService emailNumberService;
    private final EmailKakaoNumberService emailKakaoNumberService;
    private final EmailKakaoNumberRepository emailKakaoNumberRepository;


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
    public String createKakaoAccountLink(String code, Integer memberId, HttpServletResponse response) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        KakaoInfoDTO kakaoInfoDTO = getKakaoAccessToken(code);
        String token = kakaoInfoDTO.getAccessToken();


        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = conn.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            String id = String.valueOf(element.getAsJsonObject().get("id").getAsInt());
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();

            String kakaoEmail = "";
            if (hasEmail) {
                kakaoEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
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
                return "ALREADY_LINKED";  //
            }


            kakaoInfo.setKakaoEmail(kakaoEmail);
            kakaoInfo.setKakaoUserId(id);
            kakaoInfo.setKakaoAccessToken(encryptedToken);
            kakaoInfo.setJoinDay(LocalDateTime.now());
            kakaoInfo.setMember(member);
            kakaoInfo.setRefreshToken(kakaoInfoDTO.getRefreshToken());
            kakaoInfo.setAccessTokenExpires(kakaoInfoDTO.getExpiresIn());

            // KakaoInfo를 DB에 저장
            kakaoInfoRepository.save(kakaoInfo);
            emailKakaoNumberService.sendVerificationNumber(memberId, memberEmail);


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



    public String kakaoLogin(String code, HttpServletResponse response) {
        KakaoInfoDTO kakaoInfoDTO = getKakaoAccessToken(code);
        String accessToken = kakaoInfoDTO.getAccessToken();

        String kakaoEmail;
        try {
            kakaoEmail = getKakaoEmailFromToken(accessToken);
        } catch (AccessTokenExpiredException e) {
            kakaoEmail = handleExpiredAccessToken(accessToken);
        } catch (Exception e) {
            throw new RuntimeException("로그인 중 오류 발생", e);
        }

        KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);
        if (kakaoInfo == null) {
            throw new RuntimeException("해당 카카오계정을 찾을 수 없습니다.");
        }

        Member member = memberRepository.findByEmail(kakaoEmail);
        if (member == null) {
            throw new RuntimeException("해당 이메일을 가진 일반 계정을 찾을 수 없습니다.");
        }

        MemberDTO memberDTO = new MemberDTO(member);
        String jwtToken = tokenProvider.create(memberDTO);
        response.addHeader("Authorization", "Bearer " + jwtToken);

        return jwtToken;
    }

    //accessToken 만료될경우
    private String handleExpiredAccessToken(String expiredAccessToken) {
        String kakaoEmail = kakaoInfoRepository.findByKakaoAccessToken(expiredAccessToken).getKakaoEmail();
        KakaoInfo kakaoInfo = kakaoInfoRepository.findByKakaoEmail(kakaoEmail);

        String newAccessToken = refreshKakaoAccessToken(kakaoInfo.getRefreshToken());
        kakaoInfo.setKakaoAccessToken(newAccessToken);
        kakaoInfoRepository.save(kakaoInfo);

        return getKakaoEmailFromToken(newAccessToken);
    }

    private String getKakaoEmailFromToken(String accessToken) {
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            if (responseCode == 401) {
                throw new AccessTokenExpiredException("에세스 토큰 만료.");
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result ="";
            while((line = br.readLine()) != null){
                result +=line;
            }
            br.close();

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            if (hasEmail) {
                return element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            throw new RuntimeException("이메일 에러");

        } catch (IOException e) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는 중 오류가 발생", e);
        }
    }


    public class AccessTokenExpiredException extends RuntimeException {
        public AccessTokenExpiredException(String message) {
            super(message);
        }
    }



    // JWT 토큰을 response Header에 추가
    private void kakaoMemberAuthorizationInput(String jwtToken, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + jwtToken); // "Bearer " 추가
    }


}


