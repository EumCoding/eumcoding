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
@Table(name = "video_test_log")
public class VideoTestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "video_test_id")
    private VideoTest videoTest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "sub_answer")
    private String subAnswer; // 제출한 답변

    @Column(name = "scoring")
    private boolean scoring; // 제출한 답변

}
