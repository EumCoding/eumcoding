package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.service.QuestionService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/question")
@Api(tags = "Question Controller", description = "강의 질문 컨트롤러")
public class QuestionController {

    private final QuestionService questionService;
}
