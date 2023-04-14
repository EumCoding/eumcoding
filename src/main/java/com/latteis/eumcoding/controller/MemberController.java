package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.ResponseDTO;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final MemberRepository memberRepository;


    // 프로필
/*    @PostMapping("/profile")
    public ResponseEntity<?> viewProfile(@RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO responseMemberDTO = memberService.viewProfile(memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }*/


    // 로그인한 정보
    @PostMapping("/info")
    public ResponseEntity<?> viewProfile(@ApiIgnore Authentication authentication) {

        try {
            MemberDTO temp = MemberDTO.builder().id(Integer.parseInt(authentication.getPrincipal().toString())).build();
            MemberDTO responseMemberDTO = memberService.viewProfile(temp);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }

    //핸드폰번호변경
    @PostMapping("/updatetel")
    public ResponseEntity<?> updatetel(@ApiIgnore Authentication authentication, @RequestBody MemberDTO.UpdateTel updateTel) {

        try {
            String tel = memberService.updateTel(Integer.parseInt(authentication.getPrincipal().toString()), updateTel.getTel());
            if (tel != null || !tel.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().tel(tel).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 주소 변경
    @PostMapping("/updateaddress")
    public ResponseEntity<?> updateAdd(@ApiIgnore Authentication authentication,@RequestBody MemberDTO.UpdateAddress updateAddress) {

        try {
            String address = memberService.updateAddress(Integer.parseInt(authentication.getPrincipal().toString()), updateAddress.getAddress());
            if (address != null || !address.equals("")) {
                MemberDTO responseMemberDTO = MemberDTO.builder().address(address).build();
                return ResponseEntity.ok().body(responseMemberDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("error").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }




    // 비밀번호 변경하기 - 기존 비밀번호 체크 후 원하는 비밀번호로 변경하기
    @PostMapping("/updatepw")
    public ResponseEntity<?> chgPw(@ApiIgnore Authentication authentication, @RequestBody MemberDTO.UpdatePw updatePw) {
        try {
            if (memberService.updatePw(Integer.parseInt(authentication.getPrincipal().toString()), updatePw.getCurPw(),updatePw.getChgPw(), passwordEncoder)) {
                ResponseDTO responseDTO = ResponseDTO.builder().error("성공").build();
                return ResponseEntity.ok().body(responseDTO);
            } else {
                ResponseDTO responseDTO = ResponseDTO.builder().error("실패").build();
                return ResponseEntity.badRequest().body(responseDTO);
            }
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


    // 프로필 이미지 변경
    @PostMapping("/updateimgprofile")
    public ResponseEntity<?> updateProfileImg(@ApiIgnore Authentication authentication, @RequestBody MemberDTO memberDTO) {

        try {
            MemberDTO responseMemberDTO = memberService.updateProfileImg(Integer.parseInt(authentication.getPrincipal().toString()), memberDTO);
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }


}