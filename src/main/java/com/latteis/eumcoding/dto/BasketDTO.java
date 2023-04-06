package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BasketDTO {
    private int basketId;

    private int memberId;

    private int lectureId;

    private List<LectureDTO> lectureDTOList;
}
