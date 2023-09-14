package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "curriculum")
public class Curriculum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    //연관관계의 주인이 되는쪽에 @ManyToOne
    //fk가 있는쪽이 주인이되는쪽
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;


    @Column(name="time_taken")
    private int timeTaken;

    @Column(name="create_date")
    private LocalDate createDate;

    @Column(name="score")
    private int score;

    @Column(name="edit")
    private int edit;

}
