package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.MainTestDTO;
import com.latteis.eumcoding.dto.MainTestLogDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.service.MainTestLogService;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/test/question/log")
@Api(tags = "Main Test Log Controller", description = "메인 테스트 기록 컨트롤러")
public class MainTestLogController {

    private final MainTestLogService mainTestLogService;

    /*
     * MainTest 답안 저장하고 채점 결과 가져오기
     */
    @PostMapping(value = "/scoring")
    @ApiOperation(value = "MainTest 답안 저장하고 채점 결과 가져오기")
    public ResponseEntity<Integer> saveAnswerAndGetScore(@ApiIgnore Authentication authentication, @Valid @RequestBody MainTestLogDTO.ScoringDTO scoringDTO) {

        try {
            int score = mainTestLogService.saveAnswerAndGetScore(authentication, scoringDTO);
            return ResponseEntity.ok().body(score);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }


}
