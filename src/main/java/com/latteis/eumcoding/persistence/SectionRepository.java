package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SectionRepository extends JpaRepository<Section, String> {
}
