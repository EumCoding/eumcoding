package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.QuestionDTO;
import com.latteis.eumcoding.service.QuestionListService;
import com.latteis.eumcoding.service.QuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/question")
@Api(tags = "Question Controller", description = "강의 질문 컨트롤러")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionListService questionListService;
    @ApiOperation(value = "질문 등록", notes = "질문을 등록합니다.")
    @PostMapping(value = "/write")
    public ResponseEntity<?> writeQuestion(
            @ApiIgnore Authentication authentication,
            QuestionDTO.writeQuestionDTO writeQuestionDTO){

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());

        try {
            QuestionDTO.writeQuestionDTO writeQuestion = questionService.writeQuestion(memberId, writeQuestionDTO);
            return ResponseEntity.ok(writeQuestion);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에러발생");
        }

    }


    @ApiOperation(value = "질문 수정", notes = "질문을 수정합니다..")
    @PostMapping(value = "/update")
    public ResponseEntity<?> updateQuestion(
            @ApiIgnore Authentication authentication,
            QuestionDTO.updateQuestionDTO updateQuestionDTO){
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        try {
            QuestionDTO.updateQuestionDTO updateQuestionDTOS = questionService.updateQuestion(memberId,updateQuestionDTO);
            return ResponseEntity.ok(updateQuestionDTOS);
        } catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버에러발생");
        }
    }

    @ApiOperation(value = "질문 삭제", notes = "질문을 삭제합니다.")
    @PostMapping(value = "/delete")
    public ResponseEntity<?> deleteQuestion(@ApiIgnore Authentication authentication,QuestionDTO.deleteQuestionDTO deleteQuestionDTO){
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());

        try{
            questionService.deleteQuestion(memberId, deleteQuestionDTO);
            return ResponseEntity.ok().body("질문 삭제 성공");
        }catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    @ApiOperation(value = "내가 작성한 질문 가져오기", notes = "내가 작성한 질문 가져오기")
    @PostMapping("/mylist")
    public List<QuestionDTO.MyQuestionListDTO> getMyQuestions(
            @ApiIgnore Authentication authentication,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        return questionListService.getMyQuestions(memberId, start, end, page,size);
    }


    //해당 과목에 대해 질문들 가져오기
    @GetMapping("/unauth/list")
    public ResponseEntity<List<QuestionDTO.QnAQuestionListDTO>> getQuestionList(
            @RequestParam("lectureId") int lectureId,
            @RequestParam("page") int page

    ){
        List<QuestionDTO.QnAQuestionListDTO> questionList = questionListService.getQuestionList(lectureId, page);
        return ResponseEntity.ok(questionList);
    }

    //해당 과목에 대해 질문들 가져오기 - for 회원
    @GetMapping("/auth/list")
    public ResponseEntity<List<QuestionDTO.QnAQuestionListDTO>> getQuestionListForAuth(
            @ApiIgnore Authentication authentication,
            @RequestParam("lectureId") int lectureId,
            @RequestParam("page") int page

    ){
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        List<QuestionDTO.QnAQuestionListDTO> questionList = questionListService.getQuestionList(memberId, lectureId, page);
        return ResponseEntity.ok(questionList);
    }
}
