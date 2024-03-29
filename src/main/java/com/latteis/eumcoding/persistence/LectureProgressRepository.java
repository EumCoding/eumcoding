package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Integer> {



    @Query("SELECT lp FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId")
    List<LectureProgress> findByMemberId(@Param("memberId") int memberId);

   /* @Query(value = "SELECT p.member_id, m.nickname,lp.start_day " +
            "FROM lecture_progress lp, payment p, pay_lecture pl, member m " +
            "WHERE p.id = pl.payment_id AND pl.id = lp.pay_lecture_id " +
            "AND m.id = p.member_id AND pl.lecture_id = :lectureId AND lp.state = :state", nativeQuery = true)
    Page<Object[]> getStudentList(@Param("lectureId") int lectureId, @Param("state") int state, Pageable pageable);
*/
    @Query(value = "SELECT p.member.id, m.nickname,lp.startDay, lp.endDay " +
            "FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m " +
            "WHERE pl.lecture.id = :lectureId")
    Page<Object[]> getStudentList(@Param("lectureId") int lectureId, Pageable pageable);



    //video->videoProgress->lectureProgress->paylecture->lecture or payment
    @Query("SELECT lp FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId AND m.role = 0 AND pl.lecture.id = :lectureId AND p.state = 1")
    List<LectureProgress> findByMemberIdAndLectureId(@Param("memberId") int memberId, @Param("lectureId") int lectureId);

    @Query("SELECT p FROM Payment p " +
            "JOIN Member m ON p.member.id = m.id " +
            "JOIN PayLecture pl ON p.id = pl.payment.id " +
            "JOIN Lecture l ON pl.lecture.id = l.id " +
            "WHERE m.id = :memberId AND m.state = 1 " +
            "ORDER BY l.name")
    Page<LectureProgress> findAllByMemberIdAndStateOrderByName(@Param("memberId") int memberId, Pageable pageable);


    //ProfileService에 Student부분

    @Query("SELECT lp FROM LectureProgress lp WHERE lp.payLecture.payment.member.role = 0 AND lp.payLecture.payment.member.state = 1 AND lp.payLecture.payment.state = 1 AND lp.payLecture = :payLecture")
    Optional<LectureProgress> findByPayLecture(@Param("payLecture") PayLecture payLecture);


    /*
     * Member, Lecture에 맞는 Entity 가져오기
     */
    @Query("SELECT lp FROM LectureProgress lp WHERE lp.payLecture.lecture = :lecture AND lp.payLecture.payment.member = :member")
    List<LectureProgress> findByMemberAndLecture(@Param("member") Member member, @Param("lecture") Lecture lecture);

    @Query("SELECT lp FROM LectureProgress lp WHERE lp.payLecture.lecture = :lecture AND lp.payLecture.payment.member = :member AND lp.payLecture.payment.member.role = 0")
    List<LectureProgress> findByMemberLecture(@Param("member") Member member, @Param("lecture") Lecture lecture);


    @Query(value = "SELECT * " +
            "FROM lecture_progress lp " +
            "JOIN pay_lecture pl ON lp.pay_lecture_id = pl.id " +
            "JOIN payment p ON pl.payment_id = p.id " +
            "JOIN member m on p.member_id = m.id " +
            "WHERE p.state IN(0,2) AND m.id =:memberId", nativeQuery = true)
    List<LectureProgress> findByDeleteLectureProgressId(@Param("memberId") int memberId);

    /*
    * 해당 강의를 듣는 학생 리스트 가져오기
    * */
    @Query(value = "SELECT p.member " +
            "FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m " +
            "WHERE pl.lecture.id = :lectureId")
    List<Member> getStudentList(@Param("lectureId") int lectureId);
}
