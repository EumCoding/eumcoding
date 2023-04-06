package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.LectureProgress;
import com.latteis.eumcoding.model.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoProgressDTO {
    private int videoProgressId; // 사용자에게 고유하게 부여되는 값

    private int lectureProgressId;

    private int videoId;

    private int state; // 0:수강전, 1:수강시작, 2:수강중, 3:수강종료

    private LocalDateTime start_day; // 수강시작일

    private LocalDateTime end_day; // 수강종료일

    private LocalTime lastView; // 마지막 영상 위치
}
