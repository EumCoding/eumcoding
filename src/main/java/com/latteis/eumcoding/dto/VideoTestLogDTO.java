package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Video;
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
public class VideoTestLogDTO {
    private int videoTestLogId; // 사용자에게 고유하게 부여되는 값

    private int videoTestId;

    private int memberId;

    private String subAnswer; // 제출한 답변
}
