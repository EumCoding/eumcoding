package com.latteis.eumcoding.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test/question/block")
@Api(tags = "Main Test Block Controller", description = "메인 테스트 블록코딩 컨트롤러")
public class MainTestBlockController {
}
