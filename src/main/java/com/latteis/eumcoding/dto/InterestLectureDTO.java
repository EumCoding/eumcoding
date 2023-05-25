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

}
