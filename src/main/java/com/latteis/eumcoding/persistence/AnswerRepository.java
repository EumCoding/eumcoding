package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    @Query("SELECT COUNT(a) > 0 FROM Answer a WHERE a.question.id = :questionId AND a.member.role = 1")
    boolean existsByQuestion(@Param("questionId") int questionId);

    @Query("SELECT a FROM Answer a WHERE a.member = :member AND a.createdDay BETWEEN :start AND :end")
    Page<Answer> findByMemberAndCreatedDayBetween(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
}