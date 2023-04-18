package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.InterestReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestReviewRepository extends JpaRepository<InterestReview, Integer> {


    InterestReview findByIdAndMemberId(int id, int memberId);
}
