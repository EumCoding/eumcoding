package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, String> {
}
