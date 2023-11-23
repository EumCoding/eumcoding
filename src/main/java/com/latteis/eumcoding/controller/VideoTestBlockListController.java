package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureStudentDTO;
import com.latteis.eumcoding.dto.VideoTestBlockListDTO;
import com.latteis.eumcoding.service.VideoTestBlockListService;
import com.latteis.eumcoding.util.blockCoding.Block;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/lecture/section/video/test/block")
@Api(tags = "Video Test Block List Controller", description = "동영상 블록코딩 테스트")
public class VideoTestBlockListController {

    private final VideoTestBlockListService videoTestBlockListService;

    /*
    * 블록 테스트 결과 가져오기
    * 정답이면 true 아니면 false
    */
    @PostMapping(value = "/result")
    @ApiOperation(value = "블록 테스트 결과")
    public ResponseEntity<Boolean> getStudentList(@ApiIgnore Authentication authentication,
                                                  @Valid @RequestBody VideoTestBlockListDTO.TestResultRequestDTO requestDTO) {

        Boolean result = videoTestBlockListService.getBlockTestResult(authentication, requestDTO);
        return ResponseEntity.ok().body(result);

    }

}
