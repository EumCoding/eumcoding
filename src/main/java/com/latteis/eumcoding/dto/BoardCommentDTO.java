package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentDTO {
    private int boardCommentId; // 사용자에게 고유하게 부여되는 값

    private int boardId;

    private int memberId;

    private String nickname;

    private String content;

    private LocalDateTime comment_day;

    private int step;

    private int group_num;

    private int modified; // 수정여부
}
