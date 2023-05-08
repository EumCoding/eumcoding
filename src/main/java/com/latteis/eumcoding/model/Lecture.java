package com.latteis.eumcoding.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lecture")
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image; // 강좌 설명에 들어가는 이미지

    @Column(name = "price")
    private int price;

    @Column(name = "grade")
    private int grade; // 학년

    @Column(name = "created_day")
    private LocalDateTime createdDay; // 강좌생성일

    @Column(name = "thumb")
    private String thumb; // 강좌 썸네일

    @Column(name = "state")
    private int state; // 0:등록대기중, 1:등록

    @Column(name = "badge")
    private String badge; // 프로필 이미지가 들어있는 경로

    @OneToMany(mappedBy = "lecture")
    private List<Section> section;



}
