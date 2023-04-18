package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Integer> {
    ReviewComment findByIdAndMemberId(int id, int memberId);

    @Query(value = "SELECT rc.member_id, rc.content, m.nickname, rc.comment_day, rc.modified " +
            "FROM review_comment rc left join member m on m.id = rc.member_id " +
            "WHERE rc.review_id = :reviewId ", nativeQuery = true)
    Object getCommentList(@Param("reviewId") int reviewId);
}
