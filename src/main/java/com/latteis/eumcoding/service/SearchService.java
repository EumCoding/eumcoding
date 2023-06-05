package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.SearchDTO;
import com.latteis.eumcoding.dto.SearchGradeDTO;
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

    public SearchDTO searchLectures(String name, Pageable pageable) {
        Pageable updatedPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("값을 입력하세요");
        }

        List<Lecture> lectures = lectureRepository.findByNameContaining(name, updatedPageable);
        if (lectures.isEmpty()) {
            throw new NoSuchElementException("해당 강좌는 없습니다.");
        }

        // SearchDTO 인스턴스 생성
        SearchDTO searchLectures = SearchDTO.builder()
                .count(lectures.size())
                .content(new ArrayList<>())
                .build();

        for (Lecture lecture : lectures) {
            Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
            Member member = memberRepository.findById(lecture.getMember().getId()).orElse(null);

            SearchDTO.contentsDTO searchLecture = SearchDTO.contentsDTO.builder()
                    .lectureId(lecture.getId())
                    .lectureName(lecture.getName())
                    .lectureThumb(lecture.getThumb())
                    .teacherId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(member.getProfile())
                    .price(lecture.getPrice())
                    .rating(averageRating != null ? Math.round(averageRating) : 0)
                    .build();

            // DTO에 검색 결과 추가
            searchLectures.getContent().add(searchLecture);
        }

        return searchLectures;
    }


    //선생님 이름 입력했을경우, 해당 선생님이 등록한 강좌 모두 나오게
    public SearchDTO searchTeacher(String teacherName, Pageable pageable) {
        Pageable updatedPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());
        boolean teacherFound = false;

        List<Member> members = memberRepository.findByName(teacherName, updatedPageable);
        //List<SearchDTO> searchTeachers = new ArrayList<>();


        SearchDTO searchDTO = SearchDTO.builder()
                .count(members.size())
                .content(new ArrayList<>())
                .build();

        for (Member member : members) {
            // 0: 학생, 1: 선생님
            if (member.getRole() == 1) {
                teacherFound = true;
                // 선생님 이름으로 검색했을 때 해당 선생님의 모든 강의를 가져오기
                List<Lecture> resultLectures = lectureRepository.findByMemberId(member.getId());
                for (Lecture lecture : resultLectures) {
                    Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
                    Member TeacherMember = memberRepository.findById(lecture.getMember().getId()).orElse(null);

                    SearchDTO.contentsDTO searchLecture = SearchDTO.contentsDTO.builder()
                            .lectureId(lecture.getId())
                            .lectureName(lecture.getName())
                            .lectureThumb(lecture.getThumb())
                            .teacherId(TeacherMember.getId())
                            .teacherName(TeacherMember.getName())
                            .teacherProfileImage(TeacherMember.getProfile())
                            .price(lecture.getPrice())
                            .rating(averageRating != null ? Math.round(averageRating) : 0)
                            .build();

                    searchDTO.getContent().add(searchLecture);
                }
            }
        }

        if (!teacherFound) {
            throw new NoSuchElementException("해당 선생님은 존재하지 않습니다.");
        }

        return searchDTO;
    }

        //학년으로 검색햇을경우 해당 학년에 맞는 강좌가 쭈르륵 나와야함
        public SearchGradeDTO searchGrade(int grade, Pageable pageable){
            Pageable updatedPageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), pageable.getSort());

            if (grade < 1 || grade > 6) { //잘못된 학년 값 입력
                throw new IllegalArgumentException("1~6학년까지만 존재합니다.");
            }
            List<Lecture> lectures = lectureRepository.findByGrade(grade, updatedPageable);

            if(lectures.isEmpty()){
                throw new NoSuchElementException("해당 학년의 강의는 존재하지 않습니다.");
            }

            List<SearchGradeDTO> searchGradeLectures = new ArrayList<>();

            SearchGradeDTO searchGradeDTO = SearchGradeDTO.builder()
                    .count(lectures.size())
                    .content(new ArrayList<>())
                    .build();


            for (Lecture lecture : lectures) {
                Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
                Member member = memberRepository.findById(lecture.getMember().getId()).orElse(null);

                SearchGradeDTO.contentsDTO searchGrade = SearchGradeDTO.contentsDTO.builder()
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

                searchGradeDTO.getContent().add(searchGrade);
            }

            return searchGradeDTO;
        }

}