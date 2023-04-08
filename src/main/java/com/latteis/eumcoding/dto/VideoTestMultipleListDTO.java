package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.VideoTest;
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
public class VideoTestMultipleListDTO {
    private int videoTestMultipleListId; // 사용자에게 고유하게 부여되는 값

    private int videoTestId;

    private String content; // 문제의 보기

    private int sequence; // 보기의 순서
}