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
@Table(name = "lecture_progress")
public class LectureProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "pay_lecture_lecture_progress",
            joinColumns = @JoinColumn(name = "pay_lecture_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private PayLecture payLecture;

    @Column(name = "price")
    private int price;

}
