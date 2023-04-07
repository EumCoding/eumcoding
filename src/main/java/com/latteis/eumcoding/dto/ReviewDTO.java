package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Lecture;
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
public class ReviewDTO {
    private int reviewId;

    private int memberId;

    private String nickname;

    private int lectureId;

    private String lectureName;

    private int rating;

    private String content;

    private int heart;

    private LocalDateTime created_day;
}
