package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.LectureProgress;
import com.latteis.eumcoding.model.Video;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressDTO {


    // 동영상 시청 결과 요청 DTO
    @Data
    @NoArgsConstructor
    @ApiModel(value = "동영상 시청 결과 요청 DTO")
    public static class ViewedResultRequestDTO {

        @Positive(message = "양수만 가능합니다.")
        @ApiModelProperty(value = "비디오 ID", example = "1")
        private int videoId;

        @ApiModelProperty(value = "마지막 영상 위치", example = "")
        private LocalTime lastView;

    }

    private int videoProgressId; // 사용자에게 고유하게 부여되는 값

    private int lectureProgressId;

    private int videoId;

    private int state; // 0:수강전, 1:수강시작, 2:수강중, 3:수강종료

    private LocalDateTime startDay; // 수강시작일

    private LocalDateTime endDay; // 수강종료일

    private LocalTime lastView; // 마지막 영상 위치

    public static class VideoProgressState {

        // 수강 중
        public static final int STUDYING = 0;

        // 수강 완료
        public static final int COMPLETION = 1;

    }
}
