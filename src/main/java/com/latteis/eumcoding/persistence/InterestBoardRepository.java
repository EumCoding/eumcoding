package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.InterestBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestBoardRepository extends JpaRepository<InterestBoard, String> {
}
