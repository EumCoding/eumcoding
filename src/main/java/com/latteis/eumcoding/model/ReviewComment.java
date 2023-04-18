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
@Table(name = "review_comment")
public class ReviewComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

//    @ManyToOne(optional = false)
//    @JoinTable(
//            name = "member_review_comment",
//            joinColumns = @JoinColumn(name = "member_id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    )
//    private Member member;
    @Column(name = "member_id")
    @JoinColumn(name = "id")
    private int memberId;

//    @ManyToOne(optional = false)
//    @JoinTable(
//            name = "review_review_comment",
//            joinColumns = @JoinColumn(name = "review_id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    )
//    private Review review;
    @Column(name = "review_id")
    @JoinColumn(name = "id")
    private int reviewId;

    @Column(name = "content")
    private String content;


    @Column(name = "comment_day")
    private LocalDateTime commentDay;


    @Column(name = "modified")
    private int modified;

}
