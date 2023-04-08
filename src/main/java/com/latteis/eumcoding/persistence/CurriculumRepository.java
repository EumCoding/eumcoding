package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurriculumRepository extends JpaRepository<Curriculum, String> {
}
