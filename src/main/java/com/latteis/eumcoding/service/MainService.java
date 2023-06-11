package com.latteis.eumcoding.service;


import com.latteis.eumcoding.dto.MainNewLectureDTO;
import com.latteis.eumcoding.dto.MainPopularLectureDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    @Value("${file.path}")
    private String filePath;

    @Value("${file.path.lecture.image}")
    private String lecturePath;

    @Value("${server.domain}")
    private String domain;

    @Value("${server.port}")
    private String port;

    public File getMemberDirectoryPath() {
        File file = new File(filePath);
        file.mkdirs();

        return file;
    }

    public File getLectureDirectoryPath() {
        File file = new File(lecturePath);
        file.mkdirs();

        return file;
    }


    //인기강좌 5개 불러오기
    public List<MainPopularLectureDTO> getPopularLectures() {

        List<Lecture> lectures = lectureRepository.findAll();
        List<MainPopularLectureDTO> popularLectures = new ArrayList<>();

        for (Lecture lecture : lectures) {
            Integer averageRating = lectureRepository.findAverageRatingByLectureId(lecture.getId());
            Member member = memberRepository.findById(lecture.getMember().getId()).orElseThrow(() -> new NoSuchElementException("인기강좌에 해당하는 선생님이 없습니다."));

            //동점일경우 id가 낮은 경우 먼저 출력되는걸로 보임
            //lectureid : 1 avg(rating) 3
            //lectureid : 3 avg(rating) 3
            //2->1->3->4->5 순으로 출력됨
            System.out.println("강좌 평점 평균 " + lecture.getId() + ": " + averageRating);

            MainPopularLectureDTO popularLecture = MainPopularLectureDTO.builder()
                    .lectureId(lecture.getId())
                    .lectureName(lecture.getName())
                    .lectureThumb(domain + port + "/eumCodingImgs/main/lecture/" + lecture.getThumb())
                    .teacherId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(domain + port + "/eumCodingImgs/main/member/" + member.getProfile())
                    .rank(averageRating != null ? Math.round(averageRating) : 0)
                    .build();

            popularLectures.add(popularLecture);
        }

        //평점순으로 내림차순 정렬 후 순서대로 rank를 부여해야됨
        //rating : 5 인 강의 rank : 1 로 되어야함
        //평점 순으로 내림차순,
        popularLectures.sort(Comparator.comparingInt(MainPopularLectureDTO::getRank).reversed());//MainPopularLectureDTO 객체의 getRank() 메소드를 호출하여 랭크값을 기준으로 정렬한다.

        int rank = 1;
        //순서대로 rank를 부여하기
        for (MainPopularLectureDTO lecture : popularLectures){
            lecture.setRank(rank++);
        }

        //정렬된 리스트에서 상위 5개만 추출해서 리스트로 반환
        //stream은 리스트를 스트림으로 변환하고, 상위 5개만 추출하고
        //collect(Collectors.toList()) 요거는 추출된 요소를 리스트로 변환하여 반환
        return popularLectures.stream().limit(5).collect(Collectors.toList());
    }

    //신규강좌 불러오기 createdDay기준
    public List<MainNewLectureDTO> getNewLectures() {

        List<Lecture> lectures = lectureRepository.findTop5ByOrderByCreatedDayDesc();
        List<MainNewLectureDTO> newLectures = new ArrayList<>();

        for (Lecture lecture : lectures) {
            Member member = memberRepository.findById(lecture.getMember().getId()).orElseThrow(()->new NoSuchElementException("새로운 강의를 올린 선생님이 없습니다."));

            MainNewLectureDTO newLecture = MainNewLectureDTO.builder()
                    .lectureId(lecture.getId())
                    .lectureName(lecture.getName())
                    .lectureThumb(domain + port + "/eumCodingImgs/main/lecture/" + lecture.getThumb())
                    .teacherId(member.getId())
                    .teacherName(member.getName())
                    .teacherProfileImage(domain + port + "/eumCodingImgs/main/member/" + member.getProfile())
                    .build();

            newLectures.add(newLecture);
        }

        return newLectures;
    }
}