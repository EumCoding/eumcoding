package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.VideoTest;
import com.latteis.eumcoding.model.VideoTestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTestAnswerRepository extends JpaRepository<VideoTestAnswer, Integer> {

    VideoTestAnswer findById(int id);

    VideoTestAnswer findByVideoTest(VideoTest videoTest);

}
