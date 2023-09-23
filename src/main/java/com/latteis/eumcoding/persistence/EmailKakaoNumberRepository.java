package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.EmailKakaoNumber;
import com.latteis.eumcoding.model.EmailNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EmailKakaoNumberRepository extends JpaRepository<EmailKakaoNumber, Integer> {
    // 만료되지 않았으며 현재보다 이후에 만료되는 토큰정보를 가져옴
    Optional<EmailKakaoNumber> findByEmailNumberIdAndExpirationDateAfterAndExpired(int emailNumberId, LocalDateTime now, boolean expired);

    @Query(value = "SELECT e FROM EmailKakaoNumber e JOIN Member m ON e.memberId = m.id WHERE e.emailNumberId = :emailNumberId AND m.id = :memberId")
    Optional<EmailKakaoNumber> findByVerificationNumberAndMemberEmail(@Param("emailNumberId") int emailNumberId,@Param("memberId") int memberId);


    @Query(value = "SELECT * FROM email_kakao_number WHERE member_id = :memberId", nativeQuery = true)
    Optional<EmailKakaoNumber> findByMemberId(@Param("memberId") int memberId);

    @Query(value = "SELECT * FROM email_kakao_number WHERE expired = :expired", nativeQuery = true)
    List<EmailKakaoNumber> findAllByExpired(@Param("expired") int i);

    @Query(value = "SELECT * FROM email_kakao_number WHERE expired = 0 AND expiration_date < :expirationDate", nativeQuery = true)
    List<EmailKakaoNumber> findAllByExpiredFalseAndExpirationDateBefore(@Param("expirationDate") LocalDateTime expirationDate);
}
