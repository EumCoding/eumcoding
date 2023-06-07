package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@AllArgsConstructor
@Data
@Builder
public class MainTestDTO {

    @Getter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 문제 등록 요청 DTO")
    public static class AddRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int sectionId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "설명", example = "설명입니다")
        private String description; // 문제 제목

    }

    public static class MainTestType {

        // 중간 평가
        public static final int MIDTERM_EXAM = 0;

        // 최종 평가
        public static final int FINAL_EXAM = 1;

    }

}
