package com.latteis.eumcoding.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.dto.LectureDTO;
import io.swagger.annotations.ApiParam;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    @ApiParam("결제번호")
    private int paymentId;

    @ApiParam("결제회원")
    private int memberId;

    @ApiParam("결제일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime date;

    @ApiParam("결제상태")
    private int state;

    @ApiParam("결제상태를 한글로 변환")
    private String stateDescription; // 새로운 필드 추가

    @ApiParam("강좌 목록")
    private List<LectureDTO.PayLectureIdNameDTO> lectureDTOList;

    public static class PaymentState {

        // 실패
        public static final int  FAILED = 0;

        // 성공
        public static final int  SUCCESS = 1;

    }


}
