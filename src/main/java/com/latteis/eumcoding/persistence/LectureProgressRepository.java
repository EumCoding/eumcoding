package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.LectureProgress;
import com.sun.org.apache.bcel.internal.generic.VariableLengthInstruction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, Integer> {
    @Query("SELECT lp FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m WHERE m.id = :memberId")
    List<LectureProgress> findByMemberId(@Param("memberId") int memberId);

    @Query(value = "SELECT p.member_id, m.nickname,lp.start_day " +
            "FROM lecture_progress lp, payment p, pay_lecture pl, member m " +
            "WHERE p.id = pl.payment_id AND pl.id = lp.pay_lecture_id " +
            "AND m.id = p.member_id AND pl.lecture_id = :lectureId AND lp.state = :state", nativeQuery = true)
    Page<Object[]> getStudentList(@Param("lectureId") int lectureId, @Param("state") int state, Pageable pageable);

    @Query(value = "SELECT p.member.id, m.nickname,lp.start_day, lp.end_day " +
            "FROM LectureProgress lp JOIN lp.payLecture pl JOIN pl.payment p JOIN p.member m " +
            "WHERE pl.lecture.id = :lectureId")
    Page<Object[]> getStudentList1(@Param("lectureId") int lectureId, Pageable pageable);

}
