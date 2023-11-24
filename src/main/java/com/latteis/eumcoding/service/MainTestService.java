package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.persistence.MainTestRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainTestService {

    private final MainTestRepository mainTestRepository;

    private final MemberRepository memberRepository;

    private final SectionRepository sectionRepository;

    // 메인 평가 정보 리스트 가져오기 getMainTest
    public List<MainTestDTO.MainTestInfoRequestDTO> getMainTest(int lectureId) {
        // 해당 강의의 메인 평가 정보 가져오기
        List<MainTest> mainTestList = mainTestRepository.findAllBySectionLectureId(lectureId);
        // MainTestDTO.MainTestInfoRequestDTO로 변환
        List<MainTestDTO.MainTestInfoRequestDTO> responseDTO = mainTestList.stream().map(MainTestDTO.MainTestInfoRequestDTO::new).collect(java.util.stream.Collectors.toList());
        // 리턴
        return responseDTO;
    }
    // 메인 평가 등록
    public void addMainTest(int memberId, MainTestDTO.AddRequestDTO addRequestDTO) {

        // 섹션 정보 가져오기
        Section section = sectionRepository.findBySectionId(addRequestDTO.getSectionId());
        Preconditions.checkNotNull(section, "등록된 섹션이 없습니다. (섹션 ID : %s)", addRequestDTO.getSectionId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        // 본인 체크
        int lectureUploader = section.getLecture().getMember().getId();
        Preconditions.checkArgument(memberId == lectureUploader, "해당 강의의 소유자가 아닙니다. (강의 ID: %s, 강의 작성자 ID: %s, 현재 회원 ID: %s)", section.getLecture().getId(), lectureUploader, memberId);

        // 해당 유형의 평가가 이미 등록되어 있는지 검사
//        long cnt = mainTestRepository.countByTypeAndLecture(addRequestDTO.getType(), section.getLecture());
//        Preconditions.checkArgument(cnt == 0, "해당 유형의 평가가 이미 등록되어 있습니다. (type : %s)", addRequestDTO.getType());
        Preconditions.checkArgument(!mainTestRepository.existsByTypeAndSectionLecture(addRequestDTO.getType(), section.getLecture()), "해당 유형의 평가가 이미 등록되어 있습니다. (type : %s)", addRequestDTO.getType());

        // 해당 섹션에 평가가 이미 등록되어 있는지 검사
        Preconditions.checkArgument(!mainTestRepository.existsBySection(section), "해당 섹션에 평가가 이미 등록되어 있습니다. (섹션 ID : %s)", section.getId());

        MainTest mainTest = MainTest.builder()
                .section(section)
                .type(addRequestDTO.getType())
                .description(addRequestDTO.getDescription())
                .build();
        mainTestRepository.save(mainTest);

    }
}
