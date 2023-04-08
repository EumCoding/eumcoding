package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.InterestLecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestLectureRepository extends JpaRepository<InterestLecture, String> {
}
