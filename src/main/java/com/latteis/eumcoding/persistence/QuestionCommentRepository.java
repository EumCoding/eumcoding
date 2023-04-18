package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.QuestionComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Integer> {
}
