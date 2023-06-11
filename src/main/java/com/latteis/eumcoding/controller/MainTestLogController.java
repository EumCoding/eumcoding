package com.latteis.eumcoding.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test/question/log")
@Api(tags = "Main Test Log Controller", description = "메인 테스트 기록 컨트롤러")
public class MainTestLogController {
}
