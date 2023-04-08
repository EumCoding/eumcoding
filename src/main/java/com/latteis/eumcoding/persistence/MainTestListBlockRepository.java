package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.MainTestListBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainTestListBlockRepository extends JpaRepository<MainTestListBlock, String> {
}
