package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.SearchDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

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

@Slf4j
@Service
@RequiredArgsConstructor
//검색하는 서비스
//notion에 unauth/search 부분
public class SearchService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    public List<SearchDTO> searchLectures(String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isEmpty()) {
            throw new IllegalArgumentException("값을 입력하세요");
        }

        List<Lecture> lectures = lectureRepository.findByNameContaining(searchKeyword, pageable);
        if (lectures.isEmpty()) {
            throw new NoSuchElementException("해당 강좌는 없습니다.");
        }

        List<SearchDTO> searchLectures = new ArrayList<>();

        for (Lecture lecture : lectures) {
            Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
            Member member = memberRepository.findById(lecture.getMember().getId()).orElse(null);

            SearchDTO searchLecture = SearchDTO.builder()
                    .lectureId(lecture.getId())
                    .lectureName(lecture.getName())
                    .lectureThumb(lecture.getThumb())
                    .teacherId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(member.getProfile())
                    .price(lecture.getPrice())
                    .rating(averageRating != null ? Math.round(averageRating) : 0)
                    .build();

            searchLectures.add(searchLecture);
        }

        return searchLectures;
    }


    //선생님 이름 입력했을경우, 해당 선생님이 등록한 강좌 모두 나오게
    public List<SearchDTO> searchTeacher(String searchKeyword, Pageable pageable) {
        boolean teacherFound = false;

        List<Member> members = memberRepository.findByNameContaining(searchKeyword, pageable);
        List<SearchDTO> searchTeachers = new ArrayList<>();

        for (Member member : members) {
            // 0: 학생, 1: 선생님
            if (member.getRole() == 1) {
                teacherFound = true;
                // 선생님 이름으로 검색했을 때 해당 선생님의 모든 강의를 가져오기
                List<Lecture> resultLectures = lectureRepository.findByMemberId(member.getId());
                for (Lecture lecture : resultLectures) {
                    Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
                    Member TeacherMember = memberRepository.findById(lecture.getMember().getId()).orElse(null);

                    SearchDTO searchLecture = SearchDTO.builder()
                            .lectureId(lecture.getId())
                            .lectureName(lecture.getName())
                            .lectureThumb(lecture.getThumb())
                            .teacherId(TeacherMember.getId())
                            .teacherName(TeacherMember.getName())
                            .teacherProfileImage(TeacherMember.getProfile())
                            .price(lecture.getPrice())
                            .rating(averageRating != null ? Math.round(averageRating) : 0)
                            .build();

                    searchTeachers.add(searchLecture);
                }
            }
        }

        if (!teacherFound) {
            throw new NoSuchElementException("해당 선생님은 존재하지 않습니다.");
        }

        return searchTeachers;
    }

        //학년으로 검색햇을경우 해당 학년에 맞는 강좌가 쭈르륵 나와야함
        public List<SearchDTO.SearchGradeDTO> searchGrade(int searchKeyword, Pageable pageable){

            if (searchKeyword < 1 || searchKeyword > 6) { //잘못된 학년 값 입력
                throw new IllegalArgumentException("1~6학년까지만 존재합니다.");
            }
            List<Lecture> lectures = lectureRepository.findByGrade(searchKeyword, pageable);

            if(lectures.isEmpty()){
                throw new NoSuchElementException("해당 학년의 강의는 존재하지 않습니다.");
            }

            List<SearchDTO.SearchGradeDTO> searchGradeLectures = new ArrayList<>();


            for (Lecture lecture : lectures) {
                Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
                Member member = memberRepository.findById(lecture.getMember().getId()).orElse(null);

                SearchDTO.SearchGradeDTO searchGrade = SearchDTO.SearchGradeDTO.builder()
                        .lectureId(lecture.getId())
                        .lectureName(lecture.getName())
                        .lectureThumb(lecture.getThumb())
                        .teacherId(member.getId())
                        .teacherName(member.getName())
                        .teacherProfileImage(member.getProfile())
                        .price(lecture.getPrice())
                        .rating(averageRating != null ? Math.round(averageRating) : 0)
                        .grade(lecture.getGrade())
                        .build();

                searchGradeLectures.add(searchGrade);
            }

            return searchGradeLectures;
        }

}