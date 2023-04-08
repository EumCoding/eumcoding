package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.MainTestList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainTestListRepository extends JpaRepository<MainTestList, String> {
}
