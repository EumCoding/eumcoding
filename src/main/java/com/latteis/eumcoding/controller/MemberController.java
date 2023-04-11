package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.ResponseDTO;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final MemberRepository memberRepository;


    // 회원정보 수정
    @PostMapping("/info")
    public ResponseEntity<?> registerMember(MemberDTO memberDTO, @AuthenticationPrincipal String memberId) {

        try {
            MemberDTO registeredMember = memberService.editInfo(memberDTO, Integer.parseInt(memberId));
            MemberDTO responseMemberDTO = MemberDTO.builder()
                    .email(registeredMember.getEmail())
                    .nickname(registeredMember.getNickname())
                    .build();
            return ResponseEntity.ok().body(responseMemberDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }

    }


}
