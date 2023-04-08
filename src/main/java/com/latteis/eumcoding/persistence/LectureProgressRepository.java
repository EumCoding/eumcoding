package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureProgressRepository extends JpaRepository<LectureProgress, String> {
}
