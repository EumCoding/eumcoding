package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    Lecture findById(int id);

    Optional<Lecture> findByName(String name);


    //lecture entity에 private Member member로 받아서 쿼리문 저렇게 작성
    @Query("SELECT l FROM Lecture l WHERE l.member.id = :memberId")
    List<Lecture> findByMemberId(@Param("memberId") int memberId);

    //Pageable을 사용하기 위해 부득이하게 다시 만듬
    @Query("SELECT l FROM Lecture l WHERE l.member.id = :memberId")
    List<Lecture> findByMemberIdMyLecture(@Param("memberId") int memberId,Pageable pageable);

    //review 테이블 이랑 연결해서 
    //review 테이블에 rating 컬럼을 가져와서
    //이를 바탕으로 lecutre, 강좌 평점순으로 나열하기 위한 메서드
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.lecture.id = :lectureId")
    Integer findAverageRatingByLectureId(@Param("lectureId") int lectureId);

    //날짜순으로 새강좌 불러오기 5개
    @Query(value = "SELECT * FROM lecture ORDER BY created_day DESC LIMIT 5", nativeQuery = true)
    List<Lecture> findTop5ByOrderByCreatedDayDesc();

    //강좌 이름 검색
    @Query("SELECT l FROM Lecture l WHERE l.name like %:name%")
    List<Lecture> findByNameContaining(@Param("name") String name, Pageable paging);

    //갯수세기
    @Query("SELECT count(*) FROM Lecture l WHERE l.name like %:name%")
    int countByName(@Param("name") String name);


    //학년으로 검색
    @Query("SELECT l FROM Lecture l WHERE l.grade = :grade")
    List<Lecture> findByGrade(@Param("grade") int grade, Pageable paging);


    // 강의 Id로 회원Id 찾기
    @Query(value = "SELECT member_id FROM lecture WHERE id = :id", nativeQuery = true)
    int findMemberIdById(@Param("id") int lectureId);

    // 해당 lectureId와 member에 맞는 Entity가 있는지 검사
    boolean existsByIdAndMember(int id, Member member);

    Lecture findByIdAndMember(int id, Member member);

    @Query(value = "SELECT * FROM lecture WHERE id = :id AND member_id = :memberId", nativeQuery = true)
    Lecture findByIdAndMemberId(@Param("id") int id, @Param("memberId") int memberId);

    // 내가 등록한 강의 리스트 가져오기
    @Query(value = "SELECT id, name, created_day FROM lecture WHERE member_id = :memberId", nativeQuery = true)
    Page<Object[]> getUploadListByMemberId(@Param("memberId") int memberId, Pageable pageable);


    // 기간별 통계
    @Query(value = "SELECT sum(pl3.price) AS salesRevenue, " +
            "count(*) AS salesVolume, " +
            "(SELECT avg(rating) FROM review r WHERE r.lecture_id = :lectureId " +
            "AND r.created_day  >= :startDate AND r.created_day  <= :endDate) AS reviewRating " +
            "FROM lecture l, payment p, pay_lecture pl3 " +
            "WHERE l.id = pl3.lecture_id " +
            "AND p.id = pl3.payment_id " +
            "AND l.member_id = :memberId AND p.state = 1 AND l.id = :lectureId " +
            "AND p.pay_day >= :startDate AND p.pay_day <= :endDate " +
            "GROUP BY l.id", nativeQuery = true)
    Object[] getStatsByDate(@Param("memberId") int memberId,
                               @Param("lectureId") int lectureId,
                               @Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate
    );

    @Query(value = "SELECT l.id, l.name, l.price, l.createdDay, l.thumb FROM Lecture l WHERE l.member.id = :memberId")
    List<Object[]> getStatsLectureList(@Param("memberId") int memberId);
}