package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {

    // 아이디로 Board 엔티티 가져오기
    Board findById(int id);

}
