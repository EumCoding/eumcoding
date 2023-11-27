package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoTestDTO;
import com.latteis.eumcoding.service.VideoTestService;
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
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/video/test")
@Api(tags = "Video Test Controller", description = "동영상 문제 컨트롤러")
public class VideoTestController {

    private final VideoTestService videoTestService;

    // 동영상 문제 등록
    @PostMapping(value = "/add")
    @ApiOperation(value = "동영상 문제 등록")
    public ResponseEntity<Object> addTest(@ApiIgnore Authentication authentication,
                                          @Valid @RequestBody VideoTestDTO.AddRequestDTO addRequestDTO) {

        try {
            videoTestService.addTest(Integer.parseInt(authentication.getPrincipal().toString()), addRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 문제 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "동영상 문제 수정")
    public ResponseEntity<Object> updateTest(@ApiIgnore Authentication authentication,
                                          @Valid @RequestBody VideoTestDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            videoTestService.updateTest(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 문제 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "동영상 문제 삭제")
    public ResponseEntity<Object> deleteTest(@ApiIgnore Authentication authentication,
                                          @Valid @RequestBody VideoTestDTO.IdRequestDTO idRequestDTO) {

        try {
            videoTestService.deleteTest(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 문제 리스트 가져오기
    @PostMapping(value = "/list")
    @ApiOperation(value = "동영상 문제 리스트 가져오기")
    public ResponseEntity<List<VideoTestDTO.ListResponseDTO>> getVideoTestList(@ApiIgnore Authentication authentication,
                                                                             @Valid @RequestBody VideoDTO.IdRequestDTO idRequestDTO) {
        try{
            List<VideoTestDTO.ListResponseDTO> listResponseDTOList = videoTestService.getTestList(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().body(listResponseDTOList);
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }

}
