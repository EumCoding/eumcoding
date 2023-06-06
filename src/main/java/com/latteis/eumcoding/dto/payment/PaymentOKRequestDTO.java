package com.latteis.eumcoding.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.dto.LectureDTO;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOKRequestDTO {
    @ApiParam("강좌 ID")
    private int lectureId;

}
