package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.LectureStudentDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LectureStudentService {

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    private final LectureProgressRepository lectureProgressRepository;

    private final VideoRepository videoRepository;

    private final VideoProgressRepository videoProgressRepository;

    // 학생 목록 가져오기
    public List<LectureStudentDTO.ListResponseDTO> getStudentList(int memberId, LectureStudentDTO.ListRequestDTO listRequestDTO, Pageable pageable) {

        try{

            // 받아온 memberId에 맞는 MemberEntity 가져옴
            Member member = memberRepository.findByMemberId(memberId);
            // lectureId와 member에 맞는 lectureEntity 가져옴
            Lecture lecture = lectureRepository.findByIdAndMember(listRequestDTO.getLectureId(), member);
            // 아무 것도 못 가져왔다면 로그인 에러
            if (lecture == null) {
                throw new RuntimeException("LectureStudentService.getStudentList() : 로그인된 아이디는 해당 강좌의 강사가 아닙니다.");
            }

            // 해당 강의의 모든 동영상 갯수
            long totalVideoCount = videoRepository.countByLectureId(lecture.getId());
            // progress 제외한 학생dto 리스트 가져옴
            Page<Object[]> pageObjects = lectureProgressRepository.getStudentList(lecture.getId(), 1, pageable);
            // 학생 DTO List 생성
            List<LectureStudentDTO.ListResponseDTO> listResponseDTOList = new ArrayList<>();
            // 반복문
            for (Object[] object : pageObjects) {
                // 학생 DTO 생성
                LectureStudentDTO.ListResponseDTO listResponseDTO = new LectureStudentDTO.ListResponseDTO(object);
                // 시청 완료한 비디오 갯수 가져옴
                long videoProgressCount = videoProgressRepository.countByMemberIdAndLectureId(listResponseDTO.getMemberId(), lecture.getId(), 1);
                // 총 몇 퍼센트 들었는지 계산
                double progress = ((double) videoProgressCount / (double) totalVideoCount) * 100;
                // 소수점 첫번째 자리에서 반올림 후 학생 DTO에 progress 저장
                listResponseDTO.setProgress((int) Math.round(progress));
                // 학생 DTO List에 학생 DTO 저장
                listResponseDTOList.add(listResponseDTO);
            }

            return listResponseDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureStudentService.getStudentList() : 에러 발생");
        }
    }
}
