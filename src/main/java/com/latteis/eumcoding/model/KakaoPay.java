package com.latteis.eumcoding.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kakaopay")
public class KakaoPay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "kakao_payment_id")
    private String kakaoPaymentId; // 카카오페이 결제 ID

    @Column(name = "partner_order_id")
    private String partnerOrderId; // 가맹점 주문 번호

    @Column(name = "partner_user_id")
    private int partnerUserId; // 가맹점 회원 ID

    @Column(name = "total_amount")
    private int totalAmount; // 결제된 총 금액

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "tid")
    private String tid; // 결제고유번호
    
    @Column(name = "state")
    private int state; //0:실패 1:성공 2:싹제

}
