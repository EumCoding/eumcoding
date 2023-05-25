package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.InterestLectureDTO;
import com.latteis.eumcoding.model.InterestLecture;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.InterestLectureRepository;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestLectureService {

    private final InterestLectureRepository interestLectureRepository;

    private final LectureRepository lectureRepository;

    private final MemberRepository memberRepository;

    // 강의 좋아요 추가
    public void addHeart(int memberId, InterestLectureDTO.IdRequestDTO idRequestDTO) {

        // 호텔 정보 가져오기
        Lecture lecture = lectureRepository.findById(idRequestDTO.getLectureId());
        Preconditions.checkNotNull(lecture, "등록된 강의가 없습니다. (강의 ID : %s)", idRequestDTO.getLectureId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        InterestLecture interestLecture = InterestLecture.builder()
                .lecture(lecture)
                .member(member)
                .build();

        interestLectureRepository.save(interestLecture);

    }

    // 강의 좋아요 삭제
    public void deleteHeart(int memberId, InterestLectureDTO.IdRequestDTO idRequestDTO) {

        // 호텔 정보 가져오기
        InterestLecture interestLecture = interestLectureRepository.findByLectureIdAndMemberId(idRequestDTO.getLectureId(), memberId);
        Preconditions.checkNotNull(interestLecture, "등록된 좋아요가 없습니다. (강의 ID : %s)", idRequestDTO.getLectureId());

        // 등록된 회원인지 검사
        Member member = memberRepository.findByMemberId(memberId);
        Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

        interestLectureRepository.delete(interestLecture);

    }

}
