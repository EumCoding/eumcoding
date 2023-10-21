package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Payment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
public class KakaoPayReadyResponseDTO {
    private String tid; // 결제 고유 번호
    private String next_redirect_pc_url;
    private LocalDateTime created_at;
    private String partner_order_id;
    private String partner_user_id;

}
