package com.latteis.eumcoding.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.dto.LectureDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentLectureBadgeDTO {
    private long count;
    private List<PayLectureBadgeDTO> payLectureBadgeDTO;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ApiModel(value = "내가 결제한 강의의 뱃지 모음")
    public static class PayLectureBadgeDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lecutreid;

        @PositiveOrZero(message = "")
        @ApiModelProperty(value = "강의 뱃지", example = "강의 뱃지")
        private String badge;



    }

}
