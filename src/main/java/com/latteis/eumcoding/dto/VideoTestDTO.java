package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class VideoTestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int id;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 추가 요청 DTO")
    public static class AddRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 ID", example = "1")
        private int videoId;

        @ApiModelProperty(value = "문제가 배치된 시간", example = "00:05:20")
        private String testTime; // 테스트가 배치된 시간

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type; // 0:객관식 1:블록코딩

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "문제 제목", example = "문제 제목입니다")
        private String title; // 문제 제목

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "점수", example = "20")
        private int score; // 문제의 점수

        List<VideoTestMultipleListDTO.AddRequestDTO> videoTestMultipleList;

        VideoTestAnswerDTO.AddRequestDTO testAnswerDTO;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 수정 요청 DTO")
    public static class UpdateRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int Id;

        @ApiModelProperty(value = "문제가 배치된 시간", example = "00:05:20")
        private String testTime; // 테스트가 배치된 시간

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "문제 제목", example = "문제 제목입니다")
        private String title; // 문제 제목

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "점수", example = "20")
        private int score; // 문제의 점수

    }

    // 동영상 문제 가져오기
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 정보 응답 DTO")
    public static class VideoTestResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "동영상 ID", example = "1")
        private int videoId;

        @ApiModelProperty(value = "문제가 배치된 시간", example = "00:05:20")
        private String testTime; // 테스트가 배치된 시간

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type; // 0:객관식 1:블록코딩

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "문제 제목", example = "문제 제목입니다")
        private String title; // 문제 제목

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "점수", example = "20")
        private int score; // 문제의 점수

        List<VideoTestMultipleListDTO.AddRequestDTO> videoTestMultipleList;

        VideoTestAnswerDTO.AddRequestDTO testAnswerDTO;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 문제 리스트 응답 DTO")
    public static class ListResponseDTO {

        @ApiModelProperty(value = "동영상 문제 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "문제가 배치된 시간", example = "00:05:20")
        private LocalTime testTime; // 테스트가 배치된 시간

        @ApiModelProperty(value = "문제 제목", example = "문제 제목입니다")
        private String title; // 문제 제목

        @ApiModelProperty(value = "문제 유형", example = "0")
        private int type; // 0:객관식 1:블록코딩

        @ApiModelProperty(value = "점수", example = "20")
        private int score; // 문제의 점수

        List<VideoTestMultipleListDTO.ListResponseDTO> videoTestMultipleListDTOs;

        VideoTestAnswerDTO.ResponseDTO testAnswerDTO;

        public ListResponseDTO(VideoTest videoTest) {
            this.id = videoTest.getId();
            this.testTime = videoTest.getTestTime();
            this.title = videoTest.getTitle();
            this.type = videoTest.getType();
            this.score = videoTest.getScore();
        }
    }

    public static class VideoTestType {

        // 객관식 문제
        public static final int MULTIPLE_CHOICE = 0;

        // 블록 문제
        public static final int CODE_BLOCK = 1;

    }
}
