package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.util.blockCoding.Block;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainTestLogDTO {

    @Data
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 채점 요청 DTO")
    public static class ScoringDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "메인 평가 ID", example = "1")
        private int mainTestId;

        @ApiModelProperty(value = "학생 작성 답안 리스트", example = "작성 답안 리스트")
        private List<LogDTO> logDTOList;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "학생 답안 DTO")
    public static class LogDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "메인 평가 질문 ID", example = "1")
        private int mainTestQuestionId;

        @ApiModelProperty(value = "블록 코딩 답안", example = "작성 답안")
        private List<Block> blockList;

        @ApiModelProperty(value = "객관식 답안 리스트", example = "작성 답안")
        private List<String> multipleChoiceList;

    }

    @Data
    @AllArgsConstructor
    @ApiModel(value = "채점 응답 DTO")
    public static class ScoringResponseDTO {

        @ApiModelProperty(value = "학생 점수", example = "50")
        private int score;

        @ApiModelProperty(value = "만점", example = "50")
        private int perfectScore;

    }

    private int mainTestLogId; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private int mainTestListId;

    private String subAnswer; // 제출한 답변 기록
}
