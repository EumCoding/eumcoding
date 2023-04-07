package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoTestDTO {
    private int videoTestId; // 사용자에게 고유하게 부여되는 값

    private int videoId;

    private LocalTime testTime; // 테스트가 배치된 시간

    private int type; // 0:객관식 1:블록코딩

    private String title; // 문제 제목

    private int score; // 문제의 점수
}
