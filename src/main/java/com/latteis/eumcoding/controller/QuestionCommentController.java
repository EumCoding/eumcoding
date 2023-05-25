package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.AnswerDTO;
import com.latteis.eumcoding.service.QuestionCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/question/comment")
@Api(tags = "Question Comment Controller", description = "강의 질문 댓글 컨트롤러")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
    @ApiOperation(value = "질문에 대한 답변", notes = "강사만 답변을 작성할 수 있습니다.")
    @PostMapping("/write")
    public ResponseEntity<AnswerDTO.AnswerWriteDTO> writeComment(@ApiIgnore Authentication authentication,
                                                                 AnswerDTO.AnswerWriteDTO answerWriteDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        AnswerDTO.AnswerWriteDTO writeComment = questionCommentService.writeComment(memberId, answerWriteDTO);
        return ResponseEntity.ok(writeComment);
    }
    @ApiOperation(value = "질문에 대한 답변 수정", notes = "강사만 자신의 답변을 수정할 수 있습니다.")
    @PostMapping("/update")
    public ResponseEntity<AnswerDTO.AnswerUpdateDTO> updateComment(@ApiIgnore Authentication authentication,
                                                                  int answerId, AnswerDTO.AnswerUpdateDTO answerUpdateDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        AnswerDTO.AnswerUpdateDTO updateComment = questionCommentService.updateComment(memberId, answerId, answerUpdateDTO);
        return ResponseEntity.ok(updateComment);
    }

    @ApiOperation(value = "질문에 대한 답변 삭제", notes = "강사만 자신의 답변을 삭제할 수 있습니다.")
    @PostMapping("/delete")
    public ResponseEntity<AnswerDTO.AnswerDeleteDTO> deleteComment(@ApiIgnore Authentication authentication,
                                                                  int answerId) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        AnswerDTO.AnswerDeleteDTO deleteComment = questionCommentService.deleteComment(memberId,answerId);
        return ResponseEntity.ok(deleteComment);
    }
}
