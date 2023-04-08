package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<Lecture, String> {
}
