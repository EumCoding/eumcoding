package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Section;
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
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "섹션 ID 요청 DTO")
    public static class IdRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

    }

    // 섹션 추가 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "섹션 추가 요청 DTO")
    public static class AddRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @NotBlank(message = "필수 입력 값입니다.")
        @ApiModelProperty(value = "섹션명", example = "섹션명입니다")
        private String name;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 순서", example = "1")
        private int sequence;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 소요시간", example = "1")
        private int timeTaken;

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

        @ApiModelProperty(value = "섹션 ID", example = "1")
        private int id;

        @ApiModelProperty(value = "섹션 수강 완료에 걸리는 시간", example = "1000")
        private int timeTaken;

        @ApiModelProperty(value = "섹션명", example = "섹션명입니다")
        private String name;

        @ApiModelProperty(value = "섹션 순서", example = "1")
        private int sequence;

        @ApiModelProperty(value = "비디오 리스트", example = "비디오 리스트입니다")
        private List<VideoDTO.SectionListDTO> videoDTOList;

        public ListResponseDTO(Section section) {
            this.id = section.getId();
            this.timeTaken = section.getTimeTaken();
            this.name = section.getName();
            this.sequence = section.getSequence();
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SectionDTOMessageList {
        private int sectionId;
        private int lectureId;
        private String lectureName;
        private String sectionName;
        private int progress;
        private int over;
        private String overMessage;
        private String checkMessage;
    }
}