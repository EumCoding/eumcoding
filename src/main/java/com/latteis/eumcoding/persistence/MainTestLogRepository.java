package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTest;
import com.latteis.eumcoding.model.MainTestLog;
import com.latteis.eumcoding.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MainTestLogRepository extends JpaRepository<MainTestLog, String> {

    /**
     * @param member Member Entity
     * @param mainTest MainTest Entity
     * @return count
     */
    @Query(value = "SELECT COUNT(mtl) FROM MainTestLog mtl " +
            "WHERE mtl.member = :member " +
            "AND mtl.mainTestQuestion.mainTest = :mainTest ")
    long countByMemberAndMainTestQuestionMainTest(@Param("member") Member member, @Param("mainTest") MainTest mainTest);

    /**
     * @param member 학생
     * @param mainTest 메인 테스트
     * @return MainTest List
     */
    List<MainTestLog> findAllByMemberAndMainTestQuestionMainTest(Member member, MainTest mainTest);


    @Query(value = "SELECT " +
            "SUM(mtq.score * mtl.scoring) AS total_score, " +
            "COUNT(CASE WHEN mtl.scoring = 1 THEN 1 END) AS correct_answers_count, " +
            "COUNT(CASE WHEN mtl.scoring = 0 THEN 0 END) AS fail_answers_count " +
            "FROM main_test_log mtl " +
            "JOIN main_test_question mtq ON mtl.main_test_question_id = mtq.id " +
            "JOIN main_test mt ON mtq.main_test_id = mt.id " +
            "JOIN section s ON mt.section_id = s.id " +
            "JOIN lecture l ON s.lecture_id = l.id " +
            "JOIN pay_lecture pl ON l.id = pl.lecture_id " +
            "JOIN payment p ON pl.payment_id = p.id " +
            "JOIN member m ON mtl.member_id = m.id " +
            "WHERE m.id =:memberId AND l.id =:lectureId",
            nativeQuery = true)
    List<Object[]> getProgressAndTestScore(@Param("memberId")int memberId,@Param("lectureId") int lectureId);
}
