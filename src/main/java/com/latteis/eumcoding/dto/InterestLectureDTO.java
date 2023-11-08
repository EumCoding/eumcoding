package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.Positive;

@AllArgsConstructor
@Data
@Builder
public class InterestLectureDTO {

    @Getter
    @NoArgsConstructor
    @ApiModel(value = "강의 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "좋아요 수 응답 DTO")
    public static class ViewResponseDTO {

        @ApiModelProperty(value = "좋아요 수", example = "1")
        private int interestCnt;

        @ApiModelProperty(value = "좋아요 여부", example = "true")
        private boolean isInterest;

    }

}
