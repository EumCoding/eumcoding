package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDTO {

    private int sectionId;

    private String sectionName;

    private int timeTaken;

    private int mainTestId; // mainTestId가 0이 아니면 테스트가 있는 section

    private ArrayList<VideoDTO> videoList;
}
