package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, String> {
}
