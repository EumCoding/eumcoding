package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.SectionDTO;
import com.latteis.eumcoding.dto.VideoDTO;
import com.latteis.eumcoding.dto.VideoProgressDTO;
import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.service.VideoService;
import com.latteis.eumcoding.util.ProgressEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/section/video")
@Api(tags = "Video Controller", description = "비디오 컨트롤러")
public class VideoController {

        private final VideoService videoService;

        private final HttpSession httpSession;

        @PostMapping(value = "/uploadWithProgress")
        @ApiOperation(value = "동영상 업로드 및 진행률")
        public ResponseEntity<Object> uploadVideoWithProgress(@ApiIgnore Authentication authentication,
                                                              @Valid VideoDTO.UploadRequestDTO uploadRequestDTO,
                                                              @RequestPart(value = "videoFile", required = true) List<MultipartFile> videoFile,
                                                              @RequestPart(value = "thumb", required = false) List<MultipartFile> thumb) {
            try {
                videoService.uploadVideoWithProgress(Integer.parseInt(authentication.getPrincipal().toString()),
                        uploadRequestDTO, videoFile, thumb);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @GetMapping("/progress")
        public ProgressEntity getProgress() {
            return (ProgressEntity) httpSession.getAttribute("status");
        }


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

            VideoDTO.ViewResponseDTO viewResponseDTO = videoService.getVideoInfo(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().body(viewResponseDTO);

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
            e.printStackTrace();
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

    /*
    * 마지막으로 들은 영상ID 가져오기
    * 기록이 없으면 0 리턴
    */
    @PostMapping(value = "/last-view")
    @ApiOperation(value = "마지막으로 들은 영상 ID 가져오기")
    public ResponseEntity<Integer> getLastViewVideoID(@ApiIgnore Authentication authentication) {

        try {
            int lastViewVideoID = videoService.getLastViewVideoID(authentication);
            return ResponseEntity.ok().body(lastViewVideoID);
        } catch (Exception e) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

    }

}
