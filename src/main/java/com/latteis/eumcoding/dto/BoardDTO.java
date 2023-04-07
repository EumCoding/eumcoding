package com.latteis.eumcoding.dto;

import com.latteis.eumcoding.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private int boardId; // 사용자에게 고유하게 부여되는 값

    private int memberId;

    private String nickname;

    private String title;

    private String content;

    private LocalDateTime updated_day;

    private LocalDateTime created_day;

    private int type;

    private int views; // 조회수

    private List<MultipartFile> imageRequest;

    private String imageResponse;

    private int heart; // 좋아요 수

    private int commentCount; // 댓글 수
}
