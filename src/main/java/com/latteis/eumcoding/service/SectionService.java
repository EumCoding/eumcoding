package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Section;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    private final LectureRepository lectureRepository;

    // 섹션 추가
    public void addSection(int memberId, SectionDTO.AddRequestDTO addRequestDTO) {

        try {

            // 로그인된 사용자가 강의 작성자인지 검사
            int checkMemberId = lectureRepository.findMemberIdById(addRequestDTO.getLectureId());
            if (checkMemberId != memberId) {
                throw new RuntimeException("SectionService.addSection() : 로그인된 사용자는 강의 작성자가 아닙니다.");
            }

            Lecture lecture = lectureRepository.findById(addRequestDTO.getLectureId());

//            Section section = Section.builder()
            sectionRepository.save( Section.builder()
                            .lecture(lecture)
                            .timeTaken(addRequestDTO.getTimeTaken())
                            .name(addRequestDTO.getName())
                            .createdDay(LocalDateTime.now())
                            .sequence(addRequestDTO.getSequence())
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SectionService.addSection() : 에러 발생");
        }

    }
}
