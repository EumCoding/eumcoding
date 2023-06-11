package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

@AllArgsConstructor
@Data
@Builder
public class MainTestQuestionDTO {


    @Getter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 문제 등록 요청 DTO")
    public static class AddRequestDTO {



    }

    /*
    * 문제 타입
    */
    public static class QuestionType {

        // 객관식
        public static final int MULTIPLE_CHOICE = 0;

        // 블록코딩
        public static final int BLOCK_CODING = 1;

        // 주관식
        public static final int SUBJECTIVE = 2;

    }
}
