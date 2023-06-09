package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoProgressDTO;
import com.latteis.eumcoding.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/video")
@Api(tags = "Video Controller", description = "비디오 컨트롤러")
public class VideoController {

    private final VideoService videoService;

    // 동영상 업로드
    @PostMapping(value = "/upload")
    @ApiOperation(value = "동영상 업로드")
    public ResponseEntity<Object> uploadVideo(@ApiIgnore Authentication authentication,
                                              @Valid VideoDTO.UploadRequestDTO uploadRequestDTO,
                                              @RequestPart(value = "videoFile", required = false) List<MultipartFile> videoFile,
                                              @RequestPart(value = "thumb", required = false) List<MultipartFile> thumb)
    {

        try {
            videoService.uploadVideo(Integer.parseInt(authentication.getPrincipal().toString()),
                    uploadRequestDTO,
                    videoFile,
                    thumb);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "동영상 수정")
    public ResponseEntity<Object> updateVideo(@ApiIgnore Authentication authentication,
                                              @Valid VideoDTO.UpdateRequestDTO updateRequestDTO,
                                              @RequestPart(value = "videoFile", required = false) List<MultipartFile> videoFile,
                                              @RequestPart(value = "thumb", required = false) List<MultipartFile> thumb) {

        try {
            videoService.updateVideo(Integer.parseInt(authentication.getPrincipal().toString()),
                    updateRequestDTO,
                    videoFile,
                    thumb);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 동영상 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "동영상 삭제")
    public ResponseEntity<Object> deleteVideo(@ApiIgnore Authentication authentication,
                                              @Valid @RequestBody VideoDTO.IdRequestDTO idRequestDTO) {

        try {
            videoService.deleteVideo(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /*
    * 동영상 정보 불러오기
    */
    @PostMapping(value = "/view")
    @ApiOperation(value = "동영상 정보 불러오기")
    public ResponseEntity<VideoDTO.ViewResponseDTO> getVideoInfo(@ApiIgnore Authentication authentication, @Valid VideoDTO.IdRequestDTO idRequestDTO) {

        try {
            VideoDTO.ViewResponseDTO viewResponseDTO = videoService.getVideoInfo(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().body(viewResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }


    /*
    * 비디오 시청 결과
    */
    @PostMapping(value = "/view/result")
    @ApiOperation(value = "비디오 시청 결과")
    public ResponseEntity<Object> saveViewedResult(@ApiIgnore Authentication authentication, @Valid @RequestBody VideoProgressDTO.ViewedResultRequestDTO viewedResultRequestDTO) {

        try {
            videoService.saveViewedResult(Integer.parseInt(authentication.getPrincipal().toString()), viewedResultRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /*
    * 비디오 순서 앞으로 이동
    */
    @PostMapping(value = "/sequence/up")
    @ApiOperation(value = "비디오 순서 앞으로 이동")
    public ResponseEntity<Object> updateSequenceUp(@ApiIgnore Authentication authentication, @Valid @RequestBody VideoDTO.IdRequestDTO idRequestDTO) {

        try {
            videoService.updateSequenceUp(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    /*
    * 비디오 순서 뒤로 이동
    */
    @PostMapping(value = "/sequence/down")
    @ApiOperation(value = "비디오 순서 뒤로 이동")
    public ResponseEntity<Object> updateSequenceDown(@ApiIgnore Authentication authentication, @Valid @RequestBody VideoDTO.IdRequestDTO idRequestDTO) {

        try {
            videoService.updateSequenceDown(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
