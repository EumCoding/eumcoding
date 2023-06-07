package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainTestQuestionRepository extends JpaRepository<MainTestQuestion, Integer> {
}
