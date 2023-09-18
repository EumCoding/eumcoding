package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    //결제성공쿼리
    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.member.role = 0 AND p.member.state = 1 AND p.state = 1")
    List<Payment> findByMemberIdAndState(@Param("memberId") int memberId, Pageable pageable);


    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId")
    Page<Payment> findByMemberId(@Param("memberId") int memberId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.state = 1")
    List<Payment> findByMemberIdPayment(@Param("memberId") int memberId);


    @Query("SELECT p FROM Payment p JOIN Member m ON p.member.id = m.id WHERE m.id = :memberId AND m.state = 1 ")
    Page<Payment> findAllByMemberIdAndState(@Param("memberId") int memberId, Pageable pageable);

}
