package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.MainTest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainTestQuestionDTO {

    private int mainTestListId; // 사용자에게 고유하게 부여되는 값

    private int mainTestId;

    private int type; // 0:객관식, 1:블록코딩, 2:주관식

    private String question; // 문제

    private int sequence; // 순서. 0부터시작

    private int score; // 문제의 점수
}
