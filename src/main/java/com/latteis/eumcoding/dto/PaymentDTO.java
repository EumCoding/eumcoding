package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private int paymentId;

    private int memberId;

    private LocalDateTime payDay; // 결제일

    private int state; // 1: 결제성공, 2: 결제실패, 3: 결제취소

    private int price; // 총액

    private List<LectureDTO> lectureDTOList;
}
