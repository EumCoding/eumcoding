package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, Integer> {
    Basket findByMemberIdAndLectureId(int memberId, int lectureId);
}
