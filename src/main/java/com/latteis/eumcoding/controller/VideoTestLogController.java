package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.dto.VideoTestLogDTO;
import com.latteis.eumcoding.service.VideoTestLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/video/test/log")
@Api(tags = "Video Test Log Controller", description = "비디오 테스트 로그 컨트롤러")
public class VideoTestLogController {

    private final VideoTestLogService videoTestLogService;
/*
    @PostMapping(value = "/add")
    @ApiOperation(value = "동영상 테스트 로그 추가")
    public ResponseEntity<Object> addTestLog(@ApiIgnore Authentication authentication,
                                              @Valid @RequestBody VideoTestLogDTO.AddRequestDTO addRequestDTO) {

        try {
            videoTestLogService.addTestLog(Integer.parseInt(authentication.getPrincipal().toString()), addRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }*/

    @PostMapping(value = "/view")
    @ApiOperation(value = "동영상 테스트 로그 가져오기")
    public ResponseEntity<VideoTestLogDTO.ResponseDTO> addTestLog(@ApiIgnore Authentication authentication,
                                                                  @Valid @RequestBody VideoTestLogDTO.InfoRequestDTO infoRequestDTO) {

        try {
            VideoTestLogDTO.ResponseDTO responseDTO = videoTestLogService.getTestLog(Integer.parseInt(authentication.getPrincipal().toString()), infoRequestDTO);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

}
