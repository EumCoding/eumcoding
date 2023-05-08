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

    // 섹션 추가 요청 DTO
    @Getter
    @NoArgsConstructor
    @ApiModel(value = "섹션 추가 요청 DTO")
    public static class TimeTakenRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "강의 ID", example = "1")
        private int lectureId;

        @PositiveOrZero(message = "0 또는 양수만 가능합니다.")
        @ApiModelProperty(value = "섹션 수강 완료에 걸리는 시간", example = "1000")
        private int timeTaken;

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

}