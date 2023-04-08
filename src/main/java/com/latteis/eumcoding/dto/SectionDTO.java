package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Lecture;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {
    private int sectionId; // 사용자에게 고유하게 부여되는 값

    private int lectureId;

    private String lectureName;

    private int timeTaken; // 수강하는데 소요되는 시간(일)

    private String name;

    private LocalDateTime createDay;

    private int sequence; // 섹션 순서

    private int mainTestId; // mainTestId가 0이 아니면 테스트가 있는 Section임.

    private List<VideoDTO> videoDTOList;
}
