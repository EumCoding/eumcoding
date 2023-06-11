package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.dto.MainTestQuestionDTO;
import com.latteis.eumcoding.service.MainTestQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test/question")
@Api(tags = "Main Test Question Controller", description = "메인 테스트 문제 컨트롤러")
public class MainTestQuestionController {

    private final MainTestQuestionService mainTestQuestionService;
//
//    @PostMapping(value = "/add")
//    @ApiOperation(value = "메인 평가 문제 등록")
//    public ResponseEntity<Object> addMainTest(@ApiIgnore Authentication authentication,
//                                              @Valid @RequestBody MainTestQuestionDTO.AddRequestDTO addRequestDTO) {
//
//        try {
//            mainTestQuestionService.addQuestion(Integer.parseInt(authentication.getPrincipal().toString()), addRequestDTO);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//
//    }


}
