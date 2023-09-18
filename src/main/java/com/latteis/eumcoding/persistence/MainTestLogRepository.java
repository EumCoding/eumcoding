package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainTestLogRepository extends JpaRepository<MainTestLog, String> {
}
