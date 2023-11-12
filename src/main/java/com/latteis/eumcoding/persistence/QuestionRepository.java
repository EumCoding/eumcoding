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
            "JOIN member m on l.member_id = m.id AND m.role = 1 " +
            "WHERE m.id =:memberId AND q.created_day BETWEEN :start AND :end " +
            "AND (:lectureId IS NULL OR l.id = :lectureId)",nativeQuery = true)
    Page<Question> findAllByMemberAndMonthCreatedDayBetween(@Param("memberId") int memberId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end,@Param("lectureId") Integer lectureId,Pageable pageable);


}