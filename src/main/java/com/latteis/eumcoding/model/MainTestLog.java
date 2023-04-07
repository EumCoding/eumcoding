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
@Table(name = "main_test_log")
public class MainTestLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB에서 자동증가
    @Column(name = "id")
    private int id; // 사용자에게 고유하게 부여되는 값

    @ManyToOne(optional = false)
    @JoinTable(
            name = "member_main_test_log",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private Member member;

    @ManyToOne(optional = false)
    @JoinTable(
            name = "main_test_list_main_test_log",
            joinColumns = @JoinColumn(name = "main_test_list_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private MainTestList mainTestList;
    
    @Column(name = "sub_answer")
    private String snbAnswer; // 제출한 답변 기록
}
