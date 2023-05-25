package com.latteis.eumcoding.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.dto.LectureDTO;
import io.swagger.annotations.ApiParam;
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
    @ApiParam("결제번호")
    private int paymentId;
    /*
    @ApiParam("결제회원")
    private int memberId;
    */

    @ApiParam("결제일")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime date;


    @ApiParam("강좌 목록")
    private List<LectureDTO.PayLectureIdNameDTO> lectureDTOList;
}
