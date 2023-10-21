
package com.latteis.eumcoding.controller;


import com.latteis.eumcoding.dto.KakaoPayApproveResponseDTO;
import com.latteis.eumcoding.dto.KakaoPayCancelResponseDTO;
import com.latteis.eumcoding.dto.KakaoPayReadyResponseDTO;
import com.latteis.eumcoding.dto.payment.PaymentOKRequestDTO;
import com.latteis.eumcoding.security.TokenProvider;
import com.latteis.eumcoding.service.KakaoMemberService;
import com.latteis.eumcoding.service.KakaoPayService;
import com.latteis.eumcoding.service.MemberService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member/payment/kakaoPay")
public class KakaoPayController {

    @Value("${kakao.clientId}")
    private String CLIENT_ID;

    @Value("${kakao.redirectUri}")
    private String REDIRECT_URI;

    private final KakaoMemberService kakaoMemberService;


    private final TokenProvider tokenProvider;
    private final MemberService memberService;
    private final KakaoPayService kakaoPayService;


    //결제 (lectureId입력)
    @PostMapping("/ready")
    public ResponseEntity<KakaoPayReadyResponseDTO> paymentReady(@ApiIgnore Authentication authentication, @RequestBody PaymentOKRequestDTO paymentOKRequestDTO) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        KakaoPayReadyResponseDTO response = kakaoPayService.kakaoPayReady(memberId, paymentOKRequestDTO);
        return ResponseEntity.ok(response);
    }

    //결제 성공시
    @PostMapping("/success")
    public ResponseEntity<KakaoPayApproveResponseDTO> paymentApprove(@ApiIgnore Authentication authentication, @RequestParam String pgToken) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        KakaoPayApproveResponseDTO response = kakaoPayService.approveResponse(memberId, pgToken);
        return ResponseEntity.ok(response);
    }

    //결제 취소시
    @PostMapping("/cancelPayment")
    public ResponseEntity<KakaoPayCancelResponseDTO> cancelPayment(@ApiIgnore Authentication authentication,
                                                                   @RequestParam String partnerOrderId) {
        try {
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            KakaoPayCancelResponseDTO response = kakaoPayService.cancelPayment(memberId,partnerOrderId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();  // 로그 출력
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RestClientException e) {
            e.printStackTrace();  // 외부 API 호출 실패
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        } catch (Exception e) {
            e.printStackTrace();  // 기타 오류 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}


