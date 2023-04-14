package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    // Board 아이디와 작성자로 Board 엔티티 가져오기
    Board findByIdAndMemberId(int boardId, int memberId);

    // 해당 유저가 쓴 글 목록 최신순으로 가져오기
    Page<Board> findAllByMemberIdOrderByCreatedDayDesc(int memberId, Pageable pageable);


}
