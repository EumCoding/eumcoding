package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPlanInfoDTO {
    private int curriculumId;
    @JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private LocalDateTime date; //커리큘럼 섹션 시작일

    @JsonFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private LocalDateTime editDay; //커리큘럼 수정일
    //plan
    private List<SectionDTO.SectionDTOMessageList> sectionDTOList;
}
