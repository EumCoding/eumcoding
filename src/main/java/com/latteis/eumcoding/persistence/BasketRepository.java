package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Basket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasketRepository extends JpaRepository<Basket, String> {
}
