package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardCommentRepository extends JpaRepository<BoardComment, String> {
}
