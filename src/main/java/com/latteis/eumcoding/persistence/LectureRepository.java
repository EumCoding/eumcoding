package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.dto.MainPopularLectureDTO;
import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    Optional<Lecture> findById(int id);

    Optional<Lecture> findByName(String name);


    List<Lecture> findByMemberId(int memberId);

    //review 테이블 이랑 연결해서 
    //review 테이블에 rating 컬럼을 가져와서
    //이를 바탕으로 lecutre, 강좌 평점순으로 나열하기 위한 메서드
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lectureId = :lectureId")
    Integer findAverageRatingByLectureId(@Param("lectureId") int lectureId);


    //날짜순으로 새강좌 불러오기 5개
    @Query(value = "SELECT * FROM lecture ORDER BY created_day DESC LIMIT 5", nativeQuery = true)
    List<Lecture> findTop5ByOrderByCreatedDayDesc();

    //강좌 이름 검색
    List<Lecture> findByNameContaining(String searchKeyword, Pageable paging);

    //학년으로 검색
    List<Lecture> findByGrade(int searchKeyword, Pageable paging);





}