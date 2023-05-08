package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class LectureStudentDTO {

    // 학생 목록 요청 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "학생 목록 요청 DTO")
    public static class ListRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "0: 최신순, 1: 오래된순, 2: 성적높은순, 3: 성적낮은순, 4: 진도율순", example = "0")
        private int sort;

    }

    // 학생 목록 응답 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "학생 목록 응답 DTO")
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "학생 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임")
        private String nickname;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "progress", example = "progress")
        private int progress;

        @ApiModelProperty(value = "시작일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime startDay;

        public ListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.nickname = (String) objects[1];
            this.progress = (int) objects[2];
            this.startDay = timestampToLocalDateTime((Timestamp) objects[4]);
        }

        // Timestamp -> LocalDateTime 변환
        public LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
            return timestamp.toLocalDateTime();
        }

    }
}
