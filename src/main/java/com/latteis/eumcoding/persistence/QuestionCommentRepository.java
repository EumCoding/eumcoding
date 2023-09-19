package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.BoardComment;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.QuestionComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Integer> {
    @Query("SELECT COUNT(qc) > 0 FROM QuestionComment qc WHERE qc.question.id = :questionId AND qc.member.role = 1")
    boolean existsByQuestion(@Param("questionId") int questionId);

    @Query("SELECT q FROM QuestionComment q WHERE q.member = :member AND q.createdDay BETWEEN :start AND :end")
    Page<QuestionComment> findByMemberAndCreatedDayBetween(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);

    // 해당 groupNum을 가진 댓글이 있는지 검사
    boolean existsByGroupNumAndStep(int groupNum, int step);

    // groupNum과 step으로 엔티티 리스트 가져오기
    List<QuestionComment> findAllByGroupNumAndStep(int groupNum, int step);

    // groupNum으로 엔티티 리스트 가져오기
    List<QuestionComment> findAllByGroupNum(int groupNum);

    // 내가 쓴 댓글 리스트
    @Query(value = "SELECT qc.id, qc.question_id, q.title, qc.content, qc.created_day " +
            "FROM question_comment qc , question q " +
            "WHERE q.id = qc.question_id AND qc.member_id = :memberId " +
            "GROUP BY qc.id " +
            "ORDER BY qc.created_day DESC", nativeQuery = true)
    Page<Object[]> getMyList(@Param("memberId") int memberId, Pageable pageable);
}
