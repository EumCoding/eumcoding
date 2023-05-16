package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.ResponseDTO;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.EmailTokenService;
import com.latteis.eumcoding.service.UnauthMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/unauth/member")
public class UnauthMemberController {

    private final EmailTokenService emailTokenService;

    private final TokenProvider tokenProvider;

    private final UnauthMemberService unauthMemberService;

    private final PasswordEncoder passwordEncoder;


    // 회원가입
    //@RequestParam("profileImgRequest") MultipartFile profileImgRequest
    //swagger에서 테스트하려면 매개변수에 집어넣어야함
    @PostMapping("/signup")
    public ResponseEntity<?> registerMember(MemberDTO.Sign memberDTO) {

        try {
            MemberDTO registeredMember = unauthMemberService.add(memberDTO);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
            //System.out.println(registeredMember.getId() + "아이디 번호");
            //System.out.println(registeredMember + "DTO멤버");
            emailTokenService.createEmailToken(registeredMember.getId(), registeredMember.getEmail()); // 이메일 전송
            //1. 이메일 에 토큰이 날라오면 해당 토큰을 입력해야 회원가입이 진행된다.

            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }




    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody MemberDTO.loginDTO loginDTO) {

        try{

        // 로그인 성공 시에만 MemberEntity 가져옴
        MemberDTO successMemberDTO = unauthMemberService.getByCredentials(
                loginDTO.getEmail(),
                loginDTO.getPassword(),
                passwordEncoder
        );


        // MemberEntity 가져오기 성공 시
        if (successMemberDTO != null) {

            // TokenProvider 클래스를 이용해 토큰을 생성한 후 MemberDTO에 넣어서 반환
            final String token = tokenProvider.create(successMemberDTO);
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(successMemberDTO.getEmail())
                    .id(successMemberDTO.getId())
                    .token(token) 
                    .role(successMemberDTO.getRole())//멤버 타입
                    .nickname(successMemberDTO.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);

        } else {
            // MemberEntity 가져오기 실패 시 -> 로그인 실패
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("로그인 실패").build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    } catch (RuntimeException e){
            //예외발생->이메일 인증 실패,
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 이메일 중복 체크
    @PostMapping("/checkemail")
    public ResponseEntity<?> checkEmail(@RequestBody MemberDTO.CheckEmail checkEmail){
        try{
            if(unauthMemberService.checkEmail(checkEmail.getEmail())){
                ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
                System.out.println(responseDTO + "성공");
                return ResponseEntity.ok().body(responseDTO);
            }else{
                ResponseDTO responseDTO = ResponseDTO.builder().error("이메일 존재함").build();
                System.out.println(responseDTO + "에러");
                return ResponseEntity.badRequest().body(responseDTO);
            }
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            System.out.println(responseDTO + "실패");
            return ResponseEntity.badRequest().body(responseDTO);

        }
    }


    // 인증 이메일 재전송
    @PostMapping("/reconfirm")
    public ResponseEntity<?> resendConfirmEmail(@RequestBody MemberDTO.ViewConfirmEmail viewConfirmEmail){
        try{
            emailTokenService.createEmailToken(viewConfirmEmail.getId(), viewConfirmEmail.getEmail()); // 이메일 전송
            ResponseDTO responseDTO = ResponseDTO.builder().error("ok").build();
            return ResponseEntity.ok().body(responseDTO);
        }catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

}