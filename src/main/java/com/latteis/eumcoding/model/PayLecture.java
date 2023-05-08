package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pay_lecture")
public class PayLecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

/*    @ManyToOne(optional = false)
    @JoinTable(
            name = "lecture_pay_lecture",
            joinColumns = @JoinColumn(name = "lecture_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Lecture lecture;

    @ManyToOne(optional = false)
    @JoinTable(
            name = "payment_pay_lecture",
            joinColumns = @JoinColumn(name = "payment_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Payment payment;*/

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    @Column(name = "price")
    private int price; // 결제일

    @OneToMany(mappedBy = "payLecture")
    private List<LectureProgress> lectureProgresses;
}
