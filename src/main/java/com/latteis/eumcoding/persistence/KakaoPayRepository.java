package com.latteis.eumcoding.persistence;

import com.latteis.eumcoding.model.KakaoInfo;
import com.latteis.eumcoding.model.KakaoPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KakaoPayRepository extends JpaRepository<KakaoPay, Integer> {

    @Query(value = "SELECT * FROM kakaopay k JOIN member m ON k.partner_user_id = m.id WHERE k.partner_user_id =:memberId",nativeQuery = true)
    List<KakaoPay> findKakaoPayInfo(@Param("memberId") int memberId);

    @Query(value = "SELECT * FROM kakaopay k JOIN member m ON k.partner_user_id = m.id WHERE k.partner_user_id =:memberId AND k.partner_order_id =:orderId",nativeQuery = true)
    KakaoPay findKakaoPay(@Param("memberId") int memberId,@Param("orderId") String orderId);
}
