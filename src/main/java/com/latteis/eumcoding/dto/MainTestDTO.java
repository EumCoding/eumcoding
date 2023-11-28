package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.MainTestQuestion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class MainTestDTO {

    @Getter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 ID 요청 DTO")
    public static class IdDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "메인 평가 ID", example = "1")
        private int mainTestId;

    }
        // BlockDTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "BlockDTO")
    public static class BlockDTO{
        @ApiModelProperty(value = "블록", example = "블록입니다")
        private String block;

        @ApiModelProperty(value = "값", example = "값입니다")
        private String value;

        private int id;

        // Constructors
        public BlockDTO(String block, String value, int id) {
            this.block = block;
            this.value = value;
            this.id = id;
        }
    }


    @Getter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 문제 등록 요청 DTO")
    public static class AddRequestDTO {

        // lectureId
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int sectionId;

        // main test type
        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "시험 유형", example = "0")
        private int MainTestType; //0이면 중간평가, 1이면 최종평가

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "설명", example = "설명입니다")
        private String description; // 문제 제목

        //score
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "점수", example = "10")
        private int score;

        // 보기 리스트
        @ApiModelProperty(value = "보기 리스트", example = "[]")
        private List<String> choices;

        // 답
        @ApiModelProperty(value = "답", example = "[]")
        private List<String> answer;

        // 블록 리스트
        @ApiModelProperty(value = "블록 리스트", example = "[]")
        private List<BlockDTO> blockList;


    }

    public static class MainTestType {

        // 중간 평가
        public static final int MIDTERM_EXAM = 0;

        // 최종 평가
        public static final int FINAL_EXAM = 1;

    }

    // 메인 평가 정보 가져오기 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 정보 가져오기 요청 DTO")
    public static class MainTestInfoRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int sectionId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type;

        // mainTestId
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "메인 평가 ID", example = "1")
        private int mainTestId;

        // 생성자
        public MainTestInfoRequestDTO(MainTest mainTest) {
            this.sectionId = mainTest.getSection().getId();
            this.type = mainTest.getType();
            this.mainTestId = mainTest.getId();
        }

    }

    //UpdateSectionRequestDTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 섹션 수정하기 요청 DTO")
    public static class UpdateSectionRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int sectionId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int MainTestId;

        // 강의아이디
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        // type
        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type; //0이면 중간평가, 1이면 최종평가

    }

    //MainTestQuestionInfoRequestDTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "메인 평가 문제 정보 가져오기 Response DTO")
    public static class MainTestQuestionInfoRequestDTO{
        // 문제
        @ApiModelProperty(value = "문제", example = "문제입니다")
        private String title;

        // mainTestId
        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "메인 평가 ID", example = "1")
        private int mainTestId;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "문제 ID", example = "1")
        private int mainTestQuestionId;

        // 보기 리스트
        @ApiModelProperty(value = "보기 리스트", example = "[]")
        private List<String> choices;

        // 블록 리스트
        @ApiModelProperty(value = "블록 리스트", example = "[]")
        private List<BlockDTO> blockList;

        // type
        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type; // 0:객관식, 1:블록코딩, 2:주관식

        // 답안 리스트
        @ApiModelProperty(value = "답안 리스트", example = "[]")
        private List<String> answer;


        // 생성자(Entity -> DTO)
        public MainTestQuestionInfoRequestDTO(MainTestQuestion mainTestQuestion) {
            this.mainTestQuestionId = mainTestQuestion.getId();
            this.title = mainTestQuestion.getTitle();
            this.mainTestId = mainTestQuestion.getMainTest().getId();
            this.type = mainTestQuestion.getType();
        }
    }

}
