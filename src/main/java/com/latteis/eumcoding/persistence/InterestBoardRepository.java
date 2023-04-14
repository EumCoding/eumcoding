package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.InterestBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestBoardRepository extends JpaRepository<InterestBoard, Integer> {

    // 게시글 아이디와 유저 아이디로 Entity 가져오기
    InterestBoard findByBoardIdAndMemberId(int boardId, int memberId);

}
