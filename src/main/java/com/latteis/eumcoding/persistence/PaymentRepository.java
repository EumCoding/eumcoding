package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    //결제성공쿼리
    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.member.role = 0 AND p.member.state = 1 AND p.state = 1")
    List<Payment> findByMemberIdAndState(@Param("memberId") int memberId, Pageable pageable);


    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.payDay BETWEEN :startDate AND :endDate ORDER BY  p.payDay desc")
    Page<Payment>  findByMemberId(@Param("memberId") int memberId, @Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate,Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.member.id = :memberId AND p.state = 1")
    List<Payment> findByMemberIdPayment(@Param("memberId") int memberId);


    @Query("SELECT p FROM Payment p JOIN Member m ON p.member.id = m.id WHERE m.id = :memberId AND m.state = 1 ")
    Page<Payment> findAllByMemberIdAndState(@Param("memberId") int memberId, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.member.id =:memberId")
    List<Payment> findMemberId(@Param("memberId") int memberId);

    @Query("SELECT p FROM Payment p WHERE p.member.id =:memberId")
    Payment findByPaymentMemberId(int memberId);


    /**
     *내가 결제한 강좌의 뱃지 모음
     */
    @Query(value = "SELECT DISTINCT l.badge,l.id " +
            "FROM payment p " +
            "JOIN pay_lecture pl ON pl.payment_id = p.id " +
            "JOIN lecture l ON pl.lecture_id = l.id " +
            "JOIN member m ON p.member_id = m.id " +
            "WHERE m.id =:memberId", nativeQuery = true)
    List<Object[]> findByPaymentLectureBadge(@Param("memberId")int memberId);

    @Query(value = "SELECT count(*) " +
            "FROM payment p " +
            "JOIN pay_lecture pl ON pl.payment_id = p.id " +
            "JOIN lecture l ON pl.lecture_id = l.id " +
            "JOIN member m ON p.member_id = m.id " +
            "WHERE m.id =:memberId",nativeQuery = true)
    long countPaymentBadge(@Param("memberId")int memberId);
}
