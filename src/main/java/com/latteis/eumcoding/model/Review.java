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
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값
//
//    @ManyToOne(optional = false)
//    @JoinTable(
//            name = "member_review",
//            joinColumns = @JoinColumn(name = "member_id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    )
//    private Member member;
    @Column(name = "member_id")
    @JoinColumn(name = "id")
    private int memberId;

//    @ManyToOne(optional = false)
//    @JoinTable(
//            name = "lecture_review",
//            joinColumns = @JoinColumn(name = "lecture_id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    )
//    private Lecture lecture;
    @Column(name = "lecture_id")
    @JoinColumn(name = "id")
    private int lectureId;

    @Column(name = "rating")
    private int rating;

    @Column(name = "content")
    private String content;

    @Column(name = "heart")
    private int heart;

    @Column(name = "created_day")
    private LocalDateTime createdDay;
}
