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
@Table(name = "section")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "lecture_section",
            joinColumns = @JoinColumn(name = "lecture_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Lecture lecture;

    @Column(name = "time_taken")
    private int timeTaken; // 수강하는데 소요되는 시간(일)

    @Column(name = "name")
    private String name;

    @Column(name = "create_day")
    private LocalDateTime createDay;

    @Column(name = "sequence")
    private int sequence; // 섹션 순서
}