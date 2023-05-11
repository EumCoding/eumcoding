package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Lecture;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {

    // 섹션 추가 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "섹션 추가 요청 DTO")
    public static class AddRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 수강 완료에 걸리는 시간", example = "1000")
        private int timeTaken;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "섹션명", example = "섹션명입니다")
        private String name;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 순서", example = "1")
        private int sequence;

    }

    // TimeTaken 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "TimeTaken 수정 요청 DTO")
    public static class TimeTakenRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 수강 완료에 걸리는 시간", example = "1000")
        private int timeTaken;

    }

    // 섹션 이름 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "섹션 이름 수정 요청 DTO")
    public static class NameRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "섹션 이름", example = "섹션입니다")
        private String name;

    }

    // 섹션 추가 응답 DTO
    @Getter
    @Setter
    @NoArgsConstructor
    @ApiModel(value = "섹션 리스트 응답 DTO")
    public static class ListResponseDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 수강 완료에 걸리는 시간", example = "1000")
        private int timeTaken;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "섹션명", example = "섹션명입니다")
        private String name;

        @ApiModelProperty(value = "비디오 리스트", example = "비디오 리스트입니다")
        private List<VideoDTO.SectionListDTO> videoDTOList;

        public ListResponseDTO(Object[] objects) {
            this.id = (int) objects[0];
            this.timeTaken = (int) objects[1];
            this.name = (String) objects[2];
        }
    }

    private int sectionId; // 사용자에게 고유하게 부여되는 값

    private int lectureId;

    private String lectureName;

    private int timeTaken; // 수강하는데 소요되는 시간(일)

    private String name;

    private LocalDateTime createdDay;

    private int sequence; // 섹션 순서

    private int mainTestId; // mainTestId가 0이 아니면 테스트가 있는 Section임.

    private List<VideoDTO> videoDTOList;

    private int progress;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionDTOList {
        private int sectionId;
        private int lectureId;
        private String lectureName;
        private String sectionName;
        private int progress;
        private int over;
    }

}