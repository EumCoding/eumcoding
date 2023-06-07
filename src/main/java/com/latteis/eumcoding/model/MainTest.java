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
@Table(name = "main_test")
public class MainTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(name = "type")
    private int type; // 0: 중간평가, 1: 최종평가

    @Column(name = "description")
    private String description; // 설명
}
