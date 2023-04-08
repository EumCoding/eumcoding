package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, String> {
}
