package com.latteis.eumcoding.controller;


import com.latteis.eumcoding.dto.VideoTestAnswerDTO;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.service.VideoTestAnswerService;
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
@RequestMapping("/lecture/section/video/test/answer")
@Api(tags = "Video Test Answer Controller", description = "동영상 문제 답변 컨트롤러")
public class VideoTestAnswerController {

    private final VideoTestAnswerService videoTestAnswerService;

    // 동영상 문제 답변 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "동영상 문제 답변 수정")
    public ResponseEntity<Object> updateTestAnswer(@ApiIgnore Authentication authentication,
                                          @Valid @RequestBody VideoTestAnswerDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            videoTestAnswerService.updateTestAnswer(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }


}
