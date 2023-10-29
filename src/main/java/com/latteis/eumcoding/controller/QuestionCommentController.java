package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.AnswerDTO;
import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.QuestionCommentDTO;
import com.latteis.eumcoding.service.QuestionCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/question/comment")
@Api(tags = "Question Comment Controller", description = "강의 질문 댓글 컨트롤러")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
    @ApiOperation(value = "질문에 대한 답변", notes = "강사만 답변을 작성할 수 있습니다.")
    @PostMapping("/write")
    public ResponseEntity<QuestionCommentDTO.WriteRequestDTO> writeComment(@ApiIgnore Authentication authentication,
                                                                 QuestionCommentDTO.WriteRequestDTO writeRequestDTO) {

        try{
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            QuestionCommentDTO.WriteRequestDTO writeComment = questionCommentService.writeComment(memberId, writeRequestDTO);
            return ResponseEntity.ok(writeComment);
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // 대댓글 작성
    @PostMapping(value = "/write_reply")
    @ApiOperation(value = "질문 게시판 대댓글 작성")
    public ResponseEntity<QuestionCommentDTO.WriteReplyRequestDTO> writeReply(@ApiIgnore Authentication authentication, @Valid @RequestBody  QuestionCommentDTO.WriteReplyRequestDTO writeReplyRequestDTO) {

            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            QuestionCommentDTO.WriteReplyRequestDTO writeReplyComment = questionCommentService.writeReply(memberId, writeReplyRequestDTO);
            return ResponseEntity.ok(writeReplyComment);
    }

   @ApiOperation(value = "질문에 대한 답변 수정", notes = "강사만 자신의 답변을 수정할 수 있습니다.")
    @PostMapping("/update")
    public ResponseEntity<QuestionCommentDTO.updateRequestDTO> updateComment(@ApiIgnore Authentication authentication,
                                                                  int questionCommentId, QuestionCommentDTO.updateRequestDTO questionCommentUpdateDTO) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
       QuestionCommentDTO.updateRequestDTO updateComment = questionCommentService.updateComment(memberId, questionCommentId, questionCommentUpdateDTO);
        return ResponseEntity.ok(updateComment);
    }

    // 게시판 댓글 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "게시판 댓글 삭제")
    public ResponseEntity<Object> deleteComment(@ApiIgnore Authentication authentication, @Valid @RequestBody QuestionCommentDTO.deleteRequestDTO questionCommentUpdateDTO) {

        try {
            questionCommentService.deleteComment(Integer.parseInt(authentication.getPrincipal().toString()), questionCommentUpdateDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 내가 작성한 게시판 댓글 목록
    @PostMapping(value = "/my_list")
    @ApiOperation(value = "내가 쓴 게시판 댓글 리스트 가져오기")
    public ResponseEntity<List<QuestionCommentDTO.QnACommentListDTO>> getMyCommentList(@ApiIgnore Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {

        try {
            List<QuestionCommentDTO.QnACommentListDTO> myListResponseDTOS = questionCommentService.getMyCommentList(Integer.parseInt(authentication.getPrincipal().toString()), pageable);
            return ResponseEntity.ok().body(myListResponseDTOS);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // question comment list 가져오기
    @PostMapping(value = "/list")
    @ApiOperation(value = "질문 게시판 댓글 리스트 가져오기")
    public ResponseEntity<List<QuestionCommentDTO.QnACommentListDTO>> getCommentList(@ApiIgnore Authentication authentication, @RequestParam("questionId") int questionId) {

        try {
            List<QuestionCommentDTO.QnACommentListDTO> listResponseDTOS = questionCommentService.getCommentList(Integer.parseInt(authentication.getPrincipal().toString()), questionId);
            return ResponseEntity.ok().body(listResponseDTOS);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

/*
    @ApiOperation(value = "질문에 대한 답변 삭제", notes = "강사만 자신의 답변을 삭제할 수 있습니다.")
    @PostMapping("/delete")
    public ResponseEntity<AnswerDTO.AnswerDeleteDTO> deleteComment(@ApiIgnore Authentication authentication,
                                                                  int answerId) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        AnswerDTO.AnswerDeleteDTO deleteComment = questionCommentService.deleteComment(memberId,answerId);
        return ResponseEntity.ok(deleteComment);
    }


    @PostMapping("/mylist")
    @ApiOperation(value = "내 답변 목록", notes = "내 답변 목록")
    public ResponseEntity<List<QuestionCommentDTO.QnAAnswerListDTO>> getQnAAnswerList(
            @ApiIgnore Authentication authentication,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        List<QuestionCommentDTO.QnAAnswerListDTO> qnaAnswerList = questionCommentService.getQnAAnswerList(memberId, start, end, page, size);
        return ResponseEntity.ok(qnaAnswerList);
    }*/
}
