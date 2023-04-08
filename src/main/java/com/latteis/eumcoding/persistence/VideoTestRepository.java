package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTestRepository extends JpaRepository<VideoTest, String> {
}
