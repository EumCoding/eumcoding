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
@Table(name = "main_test_multiple_choice_view")
public class MainTestMultipleChoiceView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "main_test_question_id")
    private MainTestQuestion mainTestQuestion;

    @Column(name = "content")
    private String content; // 내용

    @Column(name = "sequence")
    private int sequence; // 보기 순서. 0부터.
}
