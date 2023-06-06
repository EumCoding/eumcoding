package com.latteis.eumcoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureProgressDTO {
    private int lectureProgressId;

    private int payLectureId;

    private int price;

    public static class LectureProgressState {

        // 수강중
        public static final int  STUDYING = 0;

        // 수강완료
        public static final int  COMPLETION = 1;

    }
}
