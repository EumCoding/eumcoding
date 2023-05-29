package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTestAnswer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Data
@Builder
public class VideoTestAnswerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 답안 추가 요청 DTO")
    public static class AddRequestDTO {

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "답", example = "답입니다")
        private String answer; // 문제 제목

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 답안 추가 DTO")
    public static class AddDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int videoTestId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "답", example = "답입니다")
        private String answer; // 문제 제목

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 답안 수정 DTO")
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 답변 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "답", example = "답입니다")
        private String answer; // 문제 제목

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 답안 응답 DTO")
    public static class ResponseDTO {

        @ApiModelProperty(value = "동영상 문제 답변 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "답", example = "답입니다")
        private String answer; // 문제 제목

        public ResponseDTO(VideoTestAnswer videoTestAnswer) {
            this.id = videoTestAnswer.getId();
            this.answer = videoTestAnswer.getAnswer();
        }
    }

}
