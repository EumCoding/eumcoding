package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.dto.BoardCommentDTO;
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

    List<BoardComment> findAllByBoardId(int boardId);

    @Query(value = "select bc.id, bc.member_id, m.nickname, bc.content, bc.comment_day " +
            "from board_comment bc, member m " +
            "WHERE board_id = :boardId " +
            "ORDER BY bc.comment_day DESC ", nativeQuery = true)
    List<Object[]> findAllByBoardIdOrderByCommentDay(int boardId);

}
