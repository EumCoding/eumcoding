package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainTestBlockDTO {
    private int mainTestListBlockId; // 사용자에게 고유하게 부여되는 값

    private int mainTestListId;

    private String block; // 사용자에게 고유하게 부여되는 값
}
