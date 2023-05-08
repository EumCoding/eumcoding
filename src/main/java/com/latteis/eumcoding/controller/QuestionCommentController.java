package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.service.QuestionCommentService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/question/comment")
@Api(tags = "Question Comment Controller", description = "강의 질문 댓글 컨트롤러")
public class QuestionCommentController {

    private final QuestionCommentService questionCommentService;
}
