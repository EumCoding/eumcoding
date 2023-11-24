package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTestLog;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@Data
@Builder
public class VideoTestLogDTO {

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 테스트 로그 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 테스트 로그 ID", example = "1")
        private int id;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 테스트 로그 추가 요청 DTO")
    public static class AddRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int videoTestId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "답변", example = "답변입니다")
        private String subAnswer;

        private boolean scoring;

        public AddRequestDTO(int videoTestId, String testAnswerString, boolean scoring) {
            this.videoTestId = videoTestId;
            this.subAnswer = testAnswerString;
            this.scoring = scoring;
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @ApiModel(value = "동영상 테스트 로그 가져오기 요청 DTO")
    public static class InfoRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int videoTestId;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "가져올 테스트 로그의 작성자 ID", example = "1")
        private int memberId;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 테스트 로그 응답 DTO")
    public static class ResponseDTO {

        @ApiModelProperty(value = "동영상 테스트 로그 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "답변", example = "답변입니다")
        private String subAnswer;

        public ResponseDTO(VideoTestLog videoTestLog) {
            this.id = videoTestLog.getId();
            this.subAnswer = videoTestLog.getSubAnswer();
        }
    }

}
