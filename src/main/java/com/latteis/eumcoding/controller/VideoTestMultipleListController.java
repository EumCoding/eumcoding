package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.VideoTestAnswerDTO;
import com.latteis.eumcoding.dto.VideoTestBlockListDTO;
import com.latteis.eumcoding.dto.VideoTestMultipleListDTO;
import com.latteis.eumcoding.service.VideoTestMultipleListService;
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
@RequestMapping("/lecture/section/video/test/multiple_list")
@Api(tags = "Video Test Multiple List Controller", description = "동영상 객관식 문제 보기 컨트롤러")
public class VideoTestMultipleListController {

    private final VideoTestMultipleListService videoTestMultipleListService;

    // 동영상 객관식 문제 보기 추가
    @PostMapping(value = "/add")
    @ApiOperation(value = "동영상 객관식 문제 보기 추가")
    public ResponseEntity<Object> add(@ApiIgnore Authentication authentication,
                                                   @Valid @RequestBody VideoTestMultipleListDTO.AddDTO addDTO) {

        try {
            videoTestMultipleListService.add(Integer.parseInt(authentication.getPrincipal().toString()), addDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 객관식 문제 보기 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "동영상 객관식 문제 보기 수정")
    public ResponseEntity<Object> update(@ApiIgnore Authentication authentication,
                                                   @Valid @RequestBody VideoTestMultipleListDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            videoTestMultipleListService.update(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 객관식 문제 보기 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "동영상 객관식 문제 보기 삭제")
    public ResponseEntity<Object> delete(@ApiIgnore Authentication authentication,
                                                   @Valid @RequestBody VideoTestMultipleListDTO.IdRequestDTO idRequestDTO) {

        try {
            videoTestMultipleListService.delete(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /*
     * 객관식 테스트 결과 가져오기
     * 정답이면 true 아니면 false
     */
    @PostMapping(value = "/result")
    @ApiOperation(value = "객관식 테스트 결과")
    public ResponseEntity<Boolean> getStudentList(@ApiIgnore Authentication authentication,
                                                  @Valid @RequestBody VideoTestMultipleListDTO.TestResultRequestDTO requestDTO) {

        Boolean result = videoTestMultipleListService.getTestResult(authentication, requestDTO);
        return ResponseEntity.ok().body(result);

    }


}
