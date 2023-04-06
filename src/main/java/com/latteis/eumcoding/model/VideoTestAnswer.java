package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_test_answer")
public class VideoTestAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "video_test_video_test_answer",
            joinColumns = @JoinColumn(name = "video_test_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private VideoTest videoTest;

    @Column(name = "answer")
    private String answer; // 제공될 블럭
}
