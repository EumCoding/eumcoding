package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureRepository lectureRepository;

    private final PayLectureRepository payLectureRepository;

    private final MemberRepository memberRepository;

    private final MemberService memberService;

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

    // 강의 상태 수정
    public void updateState(int memberId, LectureDTO.StateRequestDTO stateRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(stateRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setState(stateRequestDTO.getState());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateState() : 에러 발생");
        }
    }

    // 강의명 수정
    public void updateName(int memberId, LectureDTO.NameRequestDTO nameRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(nameRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setName(nameRequestDTO.getName());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateName() : 에러 발생");
        }
    }

    // 강의 설명 수정
    public void updateDescription(int memberId, LectureDTO.DescriptionRequestDTO descriptionRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(descriptionRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setDescription(descriptionRequestDTO.getDescription());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateDescription() : 에러 발생");
        }
    }

    // 강의 학년 수정
    public void updateGrade(int memberId, LectureDTO.GradeRequestDTO gradeRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(gradeRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setGrade(gradeRequestDTO.getGrade());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updateGrade() : 에러 발생");
        }
    }

    // 강의 가격 수정
    public void updatePrice(int memberId, LectureDTO.PriceRequestDTO priceRequestDTO) {

        try {

            Lecture lecture = lectureRepository.findByIdAndMemberId(priceRequestDTO.getId(), memberId);
            memberService.chkIfEntityIsEmpty(lecture);
            lecture.setPrice(priceRequestDTO.getPrice());
            lectureRepository.save(lecture);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.updatePrice() : 에러 발생");
        }
    }

    // 내가 등록한 강의 리스트 가져오기
    public List<LectureDTO.MyListResponseDTO> getMyUploadList(int memberId, Pageable pageable) {

        try {

            // 내가 등록한 강의 리스트 가져오기
            Page<Object[]> pageObjects = lectureRepository.getUploadListByMemberId(memberId, pageable);
            // 리스트 DTO 생성
            List<LectureDTO.MyListResponseDTO> myListResponseDTOList = new ArrayList<>();
            for (Object[] object : pageObjects) {
                LectureDTO.MyListResponseDTO myListResponseDTO = new LectureDTO.MyListResponseDTO(object);
                myListResponseDTOList.add(myListResponseDTO);
            }
            return myListResponseDTOList;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("LectureService.getMyUploadList() : 에러 발생");
        }

    }
}