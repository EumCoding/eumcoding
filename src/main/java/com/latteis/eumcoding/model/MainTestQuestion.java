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
@Table(name = "main_test_question")
public class MainTestQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "main_test_id")
    private MainTest mainTest;

    @Column(name = "type")
    private int type; // 0:객관식, 1:블록코딩, 2:주관식

    @Column(name = "title")
    private String title; // 문제

    @Column(name = "sequence")
    private int sequence; // 순서. 0부터시작

    @Column(name = "score")
    private int score; // 문제의 점수
}
