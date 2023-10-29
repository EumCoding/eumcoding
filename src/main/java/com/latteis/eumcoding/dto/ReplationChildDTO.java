package com.latteis.eumcoding.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.latteis.eumcoding.model.Lecture;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class ReplationChildDTO {
    private int count; //자녀 수
    private List<ReplationChildInfo> rci;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReplationChildInfo{
        private int memberId; //자녀 id
        private String email;
        private String nickname;
        private String name;
        private String profile;
    }

}
