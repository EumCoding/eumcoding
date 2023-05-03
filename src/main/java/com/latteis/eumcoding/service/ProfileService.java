package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.MemberDTO;
import com.latteis.eumcoding.dto.TeacherProfileDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
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

    public TeacherProfileDTO getTeacherProfile(int memberId) {

        Member member = memberRepository.findByIdAndRole(memberId, 1);

        if (member == null) {
            throw new NoSuchElementException("해당 선생님 프로필이 없습니다.");
        }

        List<Lecture> lectureList = lectureRepository.findByMemberId(memberId);
        List<LectureDTO.profileDTO> lectureDTOList = new ArrayList<>();
        for (Lecture lecture : lectureList) {
            LectureDTO.profileDTO lectureDTO = LectureDTO.profileDTO.builder()
                    .id(lecture.getId())
                    .memberId(lecture.getMemberId())
                    .name(lecture.getName())
                    .description(lecture.getDescription())
                    .createdDay(lecture.getCreatedDay())
                    .grade(lecture.getGrade())
                    .thumb(lecture.getThumb())
                    .price(lecture.getPrice())
                    .build();
            lectureDTOList.add(lectureDTO);
        }

            TeacherProfileDTO teacherProfileDTO = TeacherProfileDTO.builder()
                    .memberId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(member.getProfile())
                    .teacherId(member.getId())
                    .lectureDTOList(lectureDTOList)
                    .build();

            System.out.println(teacherProfileDTO + "teacherProfileDTO");
            return teacherProfileDTO;

    }


    //학생 정보 입력시 해당 학생이 어느 강의를 듣고 있는지 정보
    public MemberDTO.StudentProfileDTO getStudentProfile(int memberId) {
        Optional<Member> members = memberRepository.findById(memberId);

        if (!members.isPresent()) {
            throw new NoSuchElementException("해당 학생 정보가 없습니다.");
        }

        List<Lecture> lectureList = lectureRepository.findByMemberId(memberId);
        if (lectureList.isEmpty()) {
            throw new NoSuchElementException("해당 학생이 듣는 강좌가 없습니다.");
        }

        //학생이 강의를 여러개 들을 경우, 이를 받기 위해 리스트 타입으로 받음
        List<Integer> grades = new ArrayList<>();
        List<Integer> lectureIds = new ArrayList<>();
        List<String> lectureName = new ArrayList<>();

        for (Lecture lecture : lectureList) {
            grades.add(lecture.getGrade());
            lectureIds.add(lecture.getId());
            lectureName.add(lecture.getName());
        }

        //여러개 강의를 들을 시 즉, DB에 1번학생이 grade에 1,2,3이 저장되어 있으면
        //1,2,3으로 출력되게 해줌
        String gradesStr = grades.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String lectureIdsStr = lectureIds.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String lectureNameStr = lectureName.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));


        if (members.isPresent()) {
            Member member = members.get();
            if(member.getRole() == 0)
            {
                System.out.println(member + "member");
                MemberDTO.StudentProfileDTO studentProfileDTO = MemberDTO.StudentProfileDTO.builder()
                        .memberId(member.getId())
                        .nickname(member.getNickname())
                        .profileImage(member.getProfile())
                        .grade(gradesStr)
                        .url("")
                        .lectureId(lectureIdsStr)
                        .lectureName(lectureNameStr)
                        .build();
                System.out.println(studentProfileDTO + "studentProfileDTO");
                return studentProfileDTO;
            }
            else{
                throw new NoSuchElementException("해당 학생은 존재하지 않습니다.");
            }
        }
        return null;
    }


}