package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

}
