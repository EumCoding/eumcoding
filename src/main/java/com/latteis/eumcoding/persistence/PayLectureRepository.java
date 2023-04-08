package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.PayLecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayLectureRepository extends JpaRepository<PayLecture, String> {
}
