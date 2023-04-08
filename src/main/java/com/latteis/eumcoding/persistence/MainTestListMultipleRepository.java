package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.MainTestListMultiple;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainTestListMultipleRepository extends JpaRepository<MainTestListMultiple, String> {
}
