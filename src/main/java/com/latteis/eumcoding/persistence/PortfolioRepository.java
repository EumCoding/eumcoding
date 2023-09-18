package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioRepository extends JpaRepository<Portfolio, Integer> {

    @Query(value = "SELECT * FROM portfolio where member_id = :memberId", nativeQuery = true)
    Portfolio findByMemberId(@Param("memberId") int memberId);
}
