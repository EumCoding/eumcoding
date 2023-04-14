package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.BoardComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Integer> {

    // 댓글 아이디와 작성자에 맞는 엔티티 가져오기
    BoardComment findByIdAndMemberId(int id, int memberId);

    // groupNum으로 엔티티 리스트 가져오기
    List<BoardComment> findAllByGroupNum(int groupNum);

    // 해당 groupNum을 가진 댓글이 있는지 검사
    boolean existsByGroupNum(int groupNum);

    // boardId로 엔티티 리스트 가져오기
    List<BoardComment> findAllByBoardId(int boardId);

    // (ListReesponseDTO. 댓글 리스트) existReply를 제외한 데이터 페이징으로 담기
    @Query(value = "select bc.id, bc.member_id, m.nickname, bc.content, bc.comment_day " +
            "from board_comment bc, member m " +
            "WHERE bc.board_id = :boardId and  bc.step = :step " +
            "GROUP BY bc.id " +
            "ORDER BY bc.comment_day ", nativeQuery = true)
    Page<Object[]> getCommentList(@Param("boardId") int boardId, @Param("step") int step, Pageable pageable);

    // (ListReesponseDTO. 대댓글 리스트) existReply를 제외한 데이터 페이징으로 담기
    @Query(value = "select bc.id, bc.member_id, m.nickname, bc.content, bc.comment_day " +
            "from board_comment bc, member m " +
            "WHERE bc.group_num = :groupNum and  bc.step = :step " +
            "GROUP BY bc.id " +
            "ORDER BY bc.comment_day ", nativeQuery = true)
    Page<Object[]> getReplyList(@Param("groupNum") int groupNum, @Param("step") int step, Pageable pageable);

    // step과 groupNum에 맞는 댓글이 있으면 가져옴
    boolean existsByStepAndGroupNum(int step, int groupNum);

    // id로 해당 댓글의 step 가져옴
    @Query(value = "SELECT step FROM board_comment WHERE id = :id", nativeQuery = true)
    int getStepById(@Param("id") int id);

    // 내가 쓴 댓글 리스트
    @Query(value = "SELECT bc.id, bc.board_id, b.title, bc.content, bc.comment_day " +
            "FROM board_comment bc , board b " +
            "WHERE b.id = bc.board_id AND bc.member_id = :memberId " +
            "GROUP BY bc.id " +
            "ORDER BY bc.comment_day DESC", nativeQuery = true)
    Page<Object[]> getMyList(@Param("memberId") int memberId, Pageable pageable);
}
