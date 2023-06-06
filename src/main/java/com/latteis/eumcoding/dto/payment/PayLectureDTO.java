package com.latteis.eumcoding.dto.payment;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayLectureDTO {
    private int payLectureId; // 사용자에게 고유하게 부여되는 값

    private int lectureId;

    private int paymentId;

    private int price;
}
