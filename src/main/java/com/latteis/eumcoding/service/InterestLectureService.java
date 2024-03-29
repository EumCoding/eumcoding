package com.latteis.eumcoding.service;

import com.google.common.base.Preconditions;
import com.latteis.eumcoding.dto.InterestLectureDTO;
import com.latteis.eumcoding.dto.LectureDTO;
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
        try{
            // 강의 정보 가져오기
            Lecture lecture = lectureRepository.findById(idRequestDTO.getLectureId());
            Preconditions.checkNotNull(lecture, "등록된 강의가 없습니다. (강의 ID : %s)", idRequestDTO.getLectureId());

            // 등록된 회원인지 검사
            Member member = memberRepository.findByMemberId(memberId);
            Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

            // 이미 좋아요 한 회원인지 검사
            InterestLecture interestLecture1 = interestLectureRepository.findByLectureIdAndMemberId(idRequestDTO.getLectureId(), memberId);

            Preconditions.checkArgument(interestLecture1 == null, "이미 좋아요를 누른 회원입니다. (회원 ID : %s)", memberId);
            Preconditions.checkArgument(interestLecture1.getMember().getRole() != 0, "일반회원이 아닙니다. (회원 ID : %s)", memberId);


            InterestLecture interestLecture = InterestLecture.builder()
                    .lecture(lecture)
                    .member(member)
                    .build();

            interestLectureRepository.save(interestLecture);



        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 강의 좋아요 삭제
    public void deleteHeart(int memberId, InterestLectureDTO.IdRequestDTO idRequestDTO) {
        try{
            // 강의 정보 가져오기
            InterestLecture interestLecture = interestLectureRepository.findByLectureIdAndMemberId(idRequestDTO.getLectureId(), memberId);
            Preconditions.checkNotNull(interestLecture, "등록된 좋아요가 없습니다. (강의 ID : %s)", idRequestDTO.getLectureId());

            // 등록된 회원인지 검사
            Member member = memberRepository.findByMemberId(memberId);
            Preconditions.checkNotNull(member, "등록된 회원이 아닙니다. (회원 ID : %s)", memberId);

            // 좋아요 한적 없는 회원이면 에러 발생
            InterestLecture interestLecture1 = interestLectureRepository.findByLectureIdAndMemberId(idRequestDTO.getLectureId(), memberId);
            Preconditions.checkArgument(interestLecture1 != null, "좋아요를 누른 적이 없는 회원입니다. (회원 ID : %s)", memberId);

            interestLectureRepository.delete(interestLecture);
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    // 강의 좋아요 수 가져오기
    public InterestLectureDTO.ViewResponseDTO getInterestCnt(LectureDTO.IdRequestDTO idRequestDTO) {


        try{
            // lecture 가져오기
            Lecture lecture = lectureRepository.findById(idRequestDTO.getId());
            Preconditions.checkNotNull(lecture, "등록된 강의가 없습니다. (강의 ID : %s)", idRequestDTO.getId());

            // 해당 강의의 좋아요 수 가져오기
            long interestCnt = interestLectureRepository.countByLecture(lecture);

            // DTO 생성 후 저장
            InterestLectureDTO.ViewResponseDTO viewResponseDTO = new InterestLectureDTO.ViewResponseDTO();
            viewResponseDTO.setInterestCnt((int) interestCnt);

            return viewResponseDTO;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    // 강의 좋아요 수 가져오기 + 내가 좋아요 눌렀는지 체크
    public InterestLectureDTO.ViewResponseDTO getInterestCnt(int memberId, LectureDTO.IdRequestDTO idRequestDTO) {



        try{
            // lecture 가져오기
            Lecture lecture = lectureRepository.findById(idRequestDTO.getId());
            Preconditions.checkNotNull(lecture, "등록된 강의가 없습니다. (강의 ID : %s)", idRequestDTO.getId());

            // 해당 강의의 좋아요 수 가져오기
            long interestCnt = interestLectureRepository.countByLecture(lecture);

            // 내가 좋아요 눌렀는지 체크
            InterestLecture interestLecture = interestLectureRepository.findByLectureIdAndMemberId(idRequestDTO.getId(), memberId);
            boolean isInterest = interestLecture != null; // null이면 false, null이 아니면 true

            // DTO 생성 후 저장
            InterestLectureDTO.ViewResponseDTO viewResponseDTO = new InterestLectureDTO.ViewResponseDTO();
            viewResponseDTO.setInterestCnt((int) interestCnt);
            viewResponseDTO.setInterest(isInterest);

            return viewResponseDTO;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
