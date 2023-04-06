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
@Table(name = "video_test_multiple_list")
public class VideoTestMultipleList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "video_test_video_test_multiple_list",
            joinColumns = @JoinColumn(name = "video_test_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private VideoTest videoTest;

    @Column(name = "content")
    private String content; // 문제의 보기

    @Column(name = "sequence")
    private int sequence; // 보기의 순서
}
