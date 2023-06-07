package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.MainTestBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MainTestBlockRepository extends JpaRepository<MainTestBlock, Integer> {
}
