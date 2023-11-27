package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.MyPlanInfoDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.model.Curriculum;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.persistence.CurriculumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CurriculumServiceTest {

    @Mock
    private CurriculumRepository curriculumRepository;

    @InjectMocks
    private CurriculumService curriculumService;
    private int memberId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Curriculum testCurriculum;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

    }
    void getMyPlanInfo_ShouldReturnExpectedPlanInfo() {
        // 공통 테스트 데이터 설정
        memberId = 8;
        startDate = LocalDateTime.of(2023, 1,1, 0, 0);
        endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        testCurriculum = new Curriculum(); // 여기에 Curriculum 객체를 설정합니다.

        // 생성할 SectionDTO 목록
        List<SectionDTO.SectionDTOMessageList> sectionDTOMessageLists = new ArrayList<>();
        sectionDTOMessageLists.add(new SectionDTO.SectionDTOMessageList(
                1, 1, "테스트강조", "1과목 1주차섹션", 100, 0, 2, "해당 섹션 수강을 기한 내 완료"
        ));

        Member testMember = new Member();
        Section testSection = new Section();

        testCurriculum = Curriculum.builder()
                .id(1)
                .member(testMember) // 또는 null
                .section(testSection) // 또는 null
                .timeTaken(2)
                .score(50)
                .edit(0)
                .createDate(LocalDate.parse("2023-11-01"))
                .startDay(LocalDateTime.parse("2023-11-15T00:00:00"))
                .editDay(null) // LocalDateTime으로 파싱하거나, null로 둘 수 있습니다.
                .build();
        List<Curriculum> curriculumList = new ArrayList<>();
        List<MyPlanInfoDTO> expected = new ArrayList<>();

        // curriculumRepository.findByMemberIdAndMonthYear 메서드의 예상 결과 설정
        when(curriculumRepository.findByMemberIdAndMonthYear(memberId, startDate, endDate)).thenReturn(curriculumList);


        // Act
        List<MyPlanInfoDTO> actual = curriculumService.getMyPlanInfo(memberId, startDate, endDate);


        // Assert
        assertNotNull(actual, "결과는 null이 아니어야 합니다.");
        assertFalse(actual.isEmpty(), "결과 리스트는 비어 있지 않아야 합니다.");
        assertEquals(expected, actual, "실제 결과와 기대한 결과가 같아야 합니다.");

        // 모의 객체가 예상대로 호출되었는지 확인
        verify(curriculumRepository).findByMemberIdAndMonthYear(memberId, startDate, endDate);
    }
}