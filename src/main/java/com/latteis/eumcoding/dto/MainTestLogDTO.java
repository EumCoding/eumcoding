package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.MainTestList;
import com.latteis.eumcoding.model.Member;
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
public class MainTestLogDTO {
    private int mainTestLogId; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private int mainTestListId;

    private String snbAnswer; // 제출한 답변 기록
}
