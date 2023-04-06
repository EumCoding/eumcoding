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
public class CurriculumDTO {
    private int curriculumId;

    private int memberId;

    private int sectionId;

    private List<SectionDTO> sectionDTOList;
}
