package com.latteis.eumcoding.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test/question/multiple_choice")
@Api(tags = "Main Test Multiple Choice View Controller", description = "메인 테스트 객관식 보기 컨트롤러")
public class MainTestMultipleChoiceViewController {

}
