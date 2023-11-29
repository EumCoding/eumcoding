package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Question;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {
    @Query("SELECT q FROM Question q WHERE q.member.id = :memberId AND q.member.role = 0 AND q.lecture.id = :lectureId")
    List<Question> findQuestionsByMemberIdAndLectureId(@Param("memberId") int memberId, @Param("lectureId") int lectureId);

    Optional<Question> findById(int id);

    @Query("SELECT q FROM Question q WHERE q.member.id = :memberId AND q.createdDay BETWEEN :start AND :end")
    Page<Question> findAllByMemberAndCreatedDayBetween(@Param("memberId") int memberId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);


    @Query("SELECT q FROM Question q WHERE q.lecture.id =:lectureId")
    Page<Question> findByLectureId(@Param("lectureId") int lectureId, Pageable pageable);

    //과목에대해서 질문이 몇개있는지 카운팅
    @Query("SELECT count(distinct q.id) FROM Question q WHERE q.lecture.id =:lectureId")
    Optional<Integer> countQuestionId(@Param("lectureId") int lectureId);

    @Query(value = "SELECT q.* FROM question q " +
            "JOIN lecture l ON q.lecture_id = l.id " +
            "JOIN member m on q.member_id = m.id " +
            "WHERE q.created_day BETWEEN COALESCE(:start, '2000-01-01T00:00:00') AND COALESCE(:end, '2999-12-31T23:59:59') " +
            "AND l.id = :lectureId", nativeQuery = true)
    Page<Question> findAllByMemberAndMonthCreatedDayBetween( @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,@Param("lectureId") Integer lectureId,Pageable pageable);


    @Query(value = "SELECT count(q.id) FROM question q " +
            "JOIN lecture l ON q.lecture_id = l.id " +
            "JOIN member m on q.member_id = m.id AND m.role = 0 " +
            "WHERE q.created_day BETWEEN COALESCE(:start, '2000-01-01T00:00:00') AND COALESCE(:end, '2999-12-31T23:59:59') " +
            "AND l.id = :lectureId",nativeQuery = true)
    long countTeacherQuestions(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end,@Param("lectureId") Integer lectureId);


}