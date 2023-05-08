package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.LectureStudentDTO;
import com.latteis.eumcoding.dto.MainPopularLectureDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    private final PayLectureRepository payLectureRepository;

    private final MemberRepository memberRepository;


    // 강의 생성
    public void createLecture(int memberId, LectureDTO.CreateRequestDTO createRequestDTO) {

        try{

            Member member = memberRepository.findByMemberId(memberId);

            Lecture lecture = Lecture.builder()
                    .member(member)
                    .name(createRequestDTO.getName())
                    .description(createRequestDTO.getDescription())
                    .image("ex")
                    .price(createRequestDTO.getPrice())
                    .grade(createRequestDTO.getGrade())
                    .createdDay(LocalDateTime.now())
                    .thumb("ex")
                    .state(createRequestDTO.getState())
                    .badge("ex")
                    .build();
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.createLecture() : 에러 발생");
        }

    }

    //강의를 결제한 학생 수 구하기
    public int getTotalStudentsByLectureId(int lectureId) {
        List<PayLecture> paymentLectures = payLectureRepository.findByLectureIdAndState(lectureId, 0);
        return paymentLectures.size();
    }

}