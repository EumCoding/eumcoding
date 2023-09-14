package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.EmailNumber;
import com.latteis.eumcoding.model.EmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface EmailNumberRepository extends JpaRepository<EmailNumber, Integer> {
    // 만료되지 않았으며 현재보다 이후에 만료되는 토큰정보를 가져옴
    Optional<EmailNumber> findByEmailNumberIdAndExpirationDateAfterAndExpired(int emailNumberId, LocalDateTime now, boolean expired);

    @Query(value = "SELECT e FROM EmailNumber e JOIN Member m ON e.memberId = m.id WHERE e.emailNumberId = :emailNumberId AND m.email = :memberEmail")
    Optional<EmailNumber> findByVerificationNumberAndMemberEmail(@Param("emailNumberId") int emailNumberId,@Param("memberEmail") String memberEmail);


    @Query(value = "SELECT * FROM email_number WHERE member_id = :memberId", nativeQuery = true)
    Optional<EmailNumber> findByMemberId(@Param("memberId") int memberId);

    @Query(value = "SELECT * FROM email_number WHERE expired = :expired", nativeQuery = true)
    List<EmailNumber> findAllByExpired(@Param("expired") int i);

    @Query(value = "SELECT * FROM email_number WHERE expired = 0 AND expiration_date < :expirationDate", nativeQuery = true)
    List<EmailNumber> findAllByExpiredFalseAndExpirationDateBefore(@Param("expirationDate") LocalDateTime expirationDate);
}
