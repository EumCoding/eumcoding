package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, String> {
}
