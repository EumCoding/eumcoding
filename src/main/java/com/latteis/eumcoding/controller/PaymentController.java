package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MyPlanListDTO;
import com.latteis.eumcoding.dto.StatsDTO;
import com.latteis.eumcoding.dto.payment.PaymentDTO;
import com.latteis.eumcoding.dto.payment.PaymentOKRequestDTO;
import com.latteis.eumcoding.service.PaymentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @ApiOperation(value = "결제 완료", notes = "강의 결제를 완료하고 결제 정보를 등록")
    @PostMapping("/ok")
    public ResponseEntity<?> completePayment(@ApiIgnore Authentication authentication, @RequestBody PaymentOKRequestDTO paymentOKRequestDTO) throws Exception {

        try {
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            paymentService.completePayment(memberId, paymentOKRequestDTO);
            return ResponseEntity.ok("결제가 성공적으로 이루어졌습니다.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("없는강좌번호입니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @ApiOperation(value = "내 결제 목록", notes = "내가 결제한 목록")
    @GetMapping("/myPayment")
    public ResponseEntity<?> getMyPayments(@ApiIgnore Authentication authentication,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20230101 00:00:00") String startDateStr,
                                           @RequestParam(defaultValue = "20230930 23:59:59") String endDateStr,
                                           @RequestParam(defaultValue = "10") int size) throws Exception {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        Pageable pageable = PageRequest.of(page, size);

        // 문자열을 LocalDateTime으로 변환
        LocalDateTime startDate = LocalDateTime.parse(startDateStr, DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
        LocalDateTime endDate = LocalDateTime.parse(endDateStr, DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));

        List<PaymentDTO> paymentDTOs = paymentService.getMyPayments(memberId, startDate, endDate, pageable);
        System.out.println(paymentDTOs + "paymentDTOs");
        return ResponseEntity.ok(paymentDTOs);
    }



    @PostMapping("/cancel/{paymentId}")
    public ResponseEntity<?> cancelPayment(@ApiIgnore Authentication authentication, @PathVariable int paymentId) {
        try {
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            paymentService.cancelPayment(memberId, paymentId);
            return ResponseEntity.ok("결제가 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
