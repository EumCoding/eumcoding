package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question_comment")
public class QuestionComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "content")
    private String content;

    @Column(name = "updated_day")
    private LocalDateTime updatedDay; // 수정된 날짜

    @Column(name = "created_day")
    private LocalDateTime createdDay; // 생성된 날짜

    @Column(name = "step")
    private int step; // 댓글 깊이, 대댓글
    @Column(name = "group_num")
    private int groupNum; // 어떤 댓글에 대한 건지 번호, 루트번호
}
