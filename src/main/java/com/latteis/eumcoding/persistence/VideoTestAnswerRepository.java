package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoTestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTestAnswerRepository extends JpaRepository<VideoTestAnswer, String> {
}
