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
@Table(name = "interest_review")
public class InterestReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

//    @ManyToOne(optional = false)
//    @JoinTable(
//            name = "member_interest_review",
//            joinColumns = @JoinColumn(name = "member_id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    )
//    private Member member;
    @Column(name = "member_id")
    @JoinColumn(name = "id")
    private int memberId;
//
//    @ManyToOne(optional = false)
//    @JoinTable(
//            name = "review_interest_review",
//            joinColumns = @JoinColumn(name = "review_id"),
//            inverseJoinColumns = @JoinColumn(name = "id")
//    )
//    private Review review;
    @Column(name = "review_id")
    @JoinColumn(name = "id")
    private int reviewId;
}
