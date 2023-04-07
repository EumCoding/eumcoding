package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.MainTestList;
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
public class MainTestListMultipleDTO {
    private int mainTestListMultipleId; // 사용자에게 고유하게 부여되는 값

    private int mainTestListId;

    private String content; // 내용

    private int sequence; // 보기 순서. 0부터.
}
