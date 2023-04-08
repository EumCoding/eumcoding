package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.VideoTestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoTestLogRepository extends JpaRepository<VideoTestLog, String> {
}
