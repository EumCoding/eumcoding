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
@Table(name = "board_comment")
public class BoardComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "board_board_comment",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Board board;

    @ManyToOne(optional = false)
    @JoinTable(
            name = "member_board_comment",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Member member;

    @Column(name = "content")
    private String content;

    @Column(name = "comment_day")
    private LocalDateTime comment_day;

    @Column(name = "step")
    private int step;

    @Column(name = "group_num")
    private int group_num;

    @Column(name = "modified")
    private int modified; // 0: 수정안됨, 1: 수정됨
}
