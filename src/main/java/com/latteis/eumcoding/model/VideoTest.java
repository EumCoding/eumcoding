package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_test")
public class VideoTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(name = "test_time")
    private LocalTime testTime; // 테스트가 배치된 시간

    @Column(name = "type")
    private int type; // 0:객관식 1:블록코딩

    @Column(name = "title")
    private String title; // 문제 제목

    @Column(name = "score")
    private int score; // 문제의 점수
}
