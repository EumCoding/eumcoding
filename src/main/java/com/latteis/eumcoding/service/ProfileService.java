package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.TeacherProfileDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;

    private final LectureRepository lectureRepository;

    private final PayLectureRepository payLectureRepository;





    public TeacherProfileDTO getTeacherProfile(int memberId) {

        Member member = memberRepository.findByIdAndRole(memberId, 1);


        if (member == null) {
            throw new NoSuchElementException("해당 선생님 프로필이 없습니다.");
        }
        //memberId 여기서 널이들어같다.
        List<Lecture> lectureList = lectureRepository.findByMemberId(memberId);
        List<LectureDTO.profileDTO> lectureDTOList = new ArrayList<>();

        int totalStudent = 0;

        for (Lecture lecture : lectureList) {
            LectureDTO.profileDTO lectureDTO = LectureDTO.profileDTO.builder()
                    .id(lecture.getId())
                    .memberId(memberId)
                    .name(lecture.getName())
                    .description(lecture.getDescription())
                    .createdDay(lecture.getCreatedDay())
                    .grade(lecture.getGrade())
                    .thumb(lecture.getThumb())
                    .price(lecture.getPrice())
                    .state(lecture.getState())
                    .badge(lecture.getBadge())
                    .build();
            lectureDTOList.add(lectureDTO);

            int studentsInLecture = getTotalStudentsByLectureId(lecture.getId());
            if (studentsInLecture >= 0) {
                totalStudent += studentsInLecture;
            } else {
                throw new IllegalStateException("결제가 완료되지 않은 상태입니다.");
            }
        }


        TeacherProfileDTO teacherProfileDTO = TeacherProfileDTO.builder()
                    .memberId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(member.getProfile())
                    .teacherId(member.getId())
                    .totalLecture(lectureDTOList.size())
                    .totalStudent(totalStudent)
                    .lectureDTOList(lectureDTOList)
                    .build();

            System.out.println(teacherProfileDTO + "teacherProfileDTO");
            return teacherProfileDTO;

    }


    //학생 정보 입력시 해당 학생이 어느 강의를 듣고 있는지 정보
    public List<MemberDTO.StudentProfileDTO> getStudentProfile(int memberId) {
        Optional<Member> memberOpt = memberRepository.findById(memberId);

        if (!memberOpt.isPresent()) {
            throw new NoSuchElementException("해당 학생 정보가 없습니다.");
        }

        Member member = memberOpt.get();

        if(member.getRole() != 0) {
            throw new NoSuchElementException("해당 사용자는 학생이 아닙니다.");
        }

        List<Lecture> lectureList = payLectureRepository.findLecturesByStudentId(memberId);
        if (lectureList.isEmpty()) {
            throw new NoSuchElementException("해당 학생이 듣는 강좌가 없습니다.");
        }

        List<MemberDTO.StudentProfileDTO> profiles = new ArrayList<>();

        for (Lecture lecture : lectureList) {
            MemberDTO.StudentProfileDTO profile = MemberDTO.StudentProfileDTO.builder()
                    .memberId(member.getId())
                    .nickname(member.getNickname())
                    .profileImage(member.getProfile())
                    .grade(lecture.getGrade())
                    .url("")
                    .lectureId(lecture.getId())
                    .build();

            profiles.add(profile);
        }

        return profiles;
    }


    //강의를 결제한 학생 수 구하기
    public int getTotalStudentsByLectureId(int lectureId) {
        List<PayLecture> paymentLectures = payLectureRepository.findByLectureIdAndState(lectureId, 1);
        return paymentLectures.size();
    }

}