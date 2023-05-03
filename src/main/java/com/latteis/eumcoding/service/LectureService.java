package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MainPopularLectureDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final MemberRepository memberRepository;


    public LectureDTO getLectureById(int id) {
        Optional<Lecture> lectureOptional = lectureRepository.findById(id);
        System.out.println("확인확인확인확인확인확인확인확인확인확인확인확인확인확인" + Integer.toString(id));
        int cnt = 0;
        if (lectureOptional.isPresent()) {
            cnt++;
            Lecture lecture = lectureOptional.get();
            LectureDTO lectureDTO = LectureDTO.builder()
                    .id(lecture.getId())
                    .memberId(lecture.getMemberId())
                    .name(lecture.getName())
                    .thumb(lecture.getThumb())
                    .price(lecture.getPrice())
                    .image(lecture.getImage())
                    .description(lecture.getDescription())
                    .grade(lecture.getGrade())
                    .createdDay(lecture.getCreatedDay())
                    .build();
            System.out.println("카운트 : " + cnt);
            return lectureDTO;
        }

        return null; // 혹은 적절한 예외 처리

    }



}
