package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Review;
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
public class ReviewCommentDTO {
    private int reviewCommentId; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private String nickname;

    private int reviewId;

    private String content;

    private LocalDateTime comment_day;

    private int step;

    private int group_num;

    private int modified;
}
