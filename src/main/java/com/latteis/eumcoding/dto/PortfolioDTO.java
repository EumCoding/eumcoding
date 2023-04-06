package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
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
public class PortfolioDTO {
    private int portfolioId; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private String path; // 포트폴리오 파일이 들어가는 경로
}
