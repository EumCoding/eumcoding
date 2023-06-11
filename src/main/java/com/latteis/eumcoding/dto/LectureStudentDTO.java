package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.model.Video;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
//
//        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
//        @ApiModelProperty(value = "0: 최신순, 1: 오래된순, 2: 성적높은순, 3: 성적낮은순, 4: 진도율순", example = "0")
//        private int sort;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "학생 정보 요청 DTO")
    public static class InfoRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "학생 ID", example = "1")
        private int memberId;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

    }

    // 학생 목록 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "학생 목록 응답 DTO")
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "학생 ID", example = "1")
        private int memberId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(value = "시작일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime startDay;

        @ApiModelProperty(value = "종료일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime endDay;

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "전체 진행도. %표시", example = "progress")
        private int progress;

        public ListResponseDTO(Object[] objects) {
            this.memberId = (int) objects[0];
            this.nickname = (String) objects[1];
            this.startDay = (LocalDateTime) objects[2];
            this.endDay = (LocalDateTime) objects[3];
        }

    }

    // 학생 정보 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "학생 정보 응답 DTO")
    public static class InfoResponseDTO {

        @ApiModelProperty(value = "학생 ID", example = "1")
        private int memberId;

        @ApiModelProperty(value = "닉네임", example = "닉네임")
        private String nickname;

        @ApiModelProperty(value = "시작일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime startDay;

        @ApiModelProperty(value = "종료일", example = "2023-04-13 01:47:52.000")
        private LocalDateTime endDay;

        @ApiModelProperty(value = "전체 진행도. %표시", example = "progress")
        private int progress;

        private List<SectionListResponseDTO> sectionListResponseDTOS;

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "섹션 리스트 응답 DTO")
    public static class SectionListResponseDTO {

        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "섹션명", example = "섹션명입니다")
        private String name;

        @ApiModelProperty(value = "섹션 순서", example = "1")
        private int sequence;

        @ApiModelProperty(value = "비디오 리스트", example = "비디오 리스트입니다")
        private List<VideoListDTO> videoDTOList;

        public SectionListResponseDTO(Section section) {
            this.id = section.getId();
            this.name = section.getName();
            this.sequence = section.getSequence();
        }

    }

    @Data
    @NoArgsConstructor
    @ApiModel(value = "비디오 리스트 응답 DTO")
    public static class VideoListDTO {

        @ApiModelProperty(value = "비디오 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "비디오 이름", example = "비디오입니다")
        private String name;

        @ApiModelProperty(value = "0 : 미리보기 금지, 1 : 미리보기 가능", example = "1")
        private int preview;

        @ApiModelProperty(value = "재생시간", example = "1")
        private LocalTime playTime;

        @ApiModelProperty(value = "순서", example = "1")
        private int sequence;

        @ApiModelProperty(value = "비디오 썸네일", example = "~.png")
        private String thumb;

        public VideoListDTO(Video video) {
            this.id = video.getId();
            this.name = video.getName();
            this.preview = video.getPreview();
            this.playTime = video.getPlayTime();
            this.sequence = video.getSequence();
        }
    }

}
