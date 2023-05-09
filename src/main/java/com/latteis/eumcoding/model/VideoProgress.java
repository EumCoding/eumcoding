package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_progress")
public class VideoProgress {
    // 영상 진행 확인
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "lecture_progress_id")
    private LectureProgress lectureProgress;

    @ManyToOne(optional = false)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "state")
    private int state; // 0:수강전, 1:수강시작, 2:수강중, 3:수강종료

    @Column(name = "start_day")
    private LocalDateTime start_day; // 수강시작일

    @Column(name = "end_day")
    private LocalDateTime end_day; // 수강종료일

    @Column(name = "last_view")
    private Duration lastView; // 마지막 영상 위치
}
