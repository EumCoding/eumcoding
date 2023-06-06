package com.latteis.eumcoding.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class AnswerDTO {
   private int id;
   private int questionId;
   private int memberId;
   private String content;
   private LocalDateTime updatedDay;
   private LocalDateTime createdDay;

   @Data
   @NoArgsConstructor
   @AllArgsConstructor
   @Builder
   @ApiModel(value = "답변게시글작성")
   public static class AnswerWriteDTO {
      @ApiParam("질문 ID")
      private int questionId;
      @ApiParam("답변 내용")
      private String content;

   }

   @Data
   @Builder
   @NoArgsConstructor
   @AllArgsConstructor
   public static class AnswerUpdateDTO {

      @ApiParam("답변 ID")
      private int answerId;

      @ApiParam("답변 내용")
      private String content;

   }

   @Data
   @Builder
   @NoArgsConstructor
   @AllArgsConstructor
   public static class AnswerDeleteDTO {

      @ApiParam("답변 ID")
      private int answerId;
   }

}
