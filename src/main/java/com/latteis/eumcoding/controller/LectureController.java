package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.service.LectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
@RequestMapping("/lecture")
@Api(tags = "Lecture Controller", description = "강의 컨트롤러")
public class LectureController {

    private final LectureService lectureService;

    // 강의 생성
    @PostMapping(value = "/create")
    @ApiOperation(value = "강의 생성")
    public ResponseEntity<Object> createLecture(@ApiIgnore Authentication authentication,
                                                @Valid LectureDTO.CreateRequestDTO createRequestDTO,
                                                @RequestPart(value = "image", required = false) List<MultipartFile> image,
                                                @RequestPart(value = "thumb", required = false) List<MultipartFile> thumb,
                                                @RequestPart(value = "badge", required = false) List<MultipartFile> badge) {

        try {
            lectureService.createLecture(Integer.parseInt(authentication.getPrincipal().toString()),
                                        createRequestDTO,
                                        image,
                                        thumb,
                                        badge);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 상태 수정
    @PostMapping(value = "/update/state")
    @ApiOperation(value = "강의 상태 수정")
    public ResponseEntity<Object> updateState(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.StateRequestDTO stateRequestDTO) {

        try {
            lectureService.updateState(Integer.parseInt(authentication.getPrincipal().toString()), stateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의명 수정
    @PostMapping(value = "/update/name")
    @ApiOperation(value = "강의명 수정")
    public ResponseEntity<Object> updateName(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.NameRequestDTO nameRequestDTO) {

        try {
            lectureService.updateName(Integer.parseInt(authentication.getPrincipal().toString()), nameRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 설명 수정
    @PostMapping(value = "/update/description")
    @ApiOperation(value = "강의 설명 수정")
    public ResponseEntity<Object> updateDescription(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.DescriptionRequestDTO descriptionRequestDTO) {

        try {
            lectureService.updateDescription(Integer.parseInt(authentication.getPrincipal().toString()), descriptionRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 학년 수정
    @PostMapping(value = "/update/grade")
    @ApiOperation(value = "강의 학년 수정")
    public ResponseEntity<Object> updateGrade(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.GradeRequestDTO gradeRequestDTO) {

        try {
            lectureService.updateGrade(Integer.parseInt(authentication.getPrincipal().toString()), gradeRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 가격 수정
    @PostMapping(value = "/update/price")
    @ApiOperation(value = "강의 가격 수정")
    public ResponseEntity<Object> updatePrice(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.PriceRequestDTO priceRequestDTO) {

        try {
            lectureService.updatePrice(Integer.parseInt(authentication.getPrincipal().toString()), priceRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 설명 이미지 수정
    @PostMapping(value = "/update/image")
    @ApiOperation(value = "강의 설명 이미지 수정")
    public ResponseEntity<Object> updateImage(@ApiIgnore Authentication authentication,
                                              @Valid LectureDTO.IdRequestDTO idRequestDTO,
                                              @RequestPart(value = "image", required = false) List<MultipartFile> image) {

        try {
            lectureService.updateImage(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO, image);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 썸네일 이미지 수정
    @PostMapping(value = "/update/thumb")
    @ApiOperation(value = "강의 썸네일 이미지 수정")
    public ResponseEntity<Object> updateThumb(@ApiIgnore Authentication authentication,
                                              @Valid LectureDTO.IdRequestDTO idRequestDTO,
                                              @RequestPart(value = "thumb", required = false) List<MultipartFile> thumb) {

        try {
            lectureService.updateThumb(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO, thumb);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 뱃지 이미지 수정
    @PostMapping(value = "/update/badge")
    @ApiOperation(value = "강의 뱃지 이미지 수정")
    public ResponseEntity<Object> updateBadge(@ApiIgnore Authentication authentication,
                                              @Valid LectureDTO.IdRequestDTO idRequestDTO,
                                              @RequestPart(value = "badge", required = false) List<MultipartFile> badge) {

        try {
            lectureService.updateBadge(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO, badge);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 내가 등록한 강의 리스트 가져오기
    @PostMapping(value = "/upload_list")
    @ApiOperation(value = "내가 등록한 강의 리스트")
    public ResponseEntity<List<LectureDTO.MyListResponseDTO>> getMyUploadList(@ApiIgnore Authentication authentication, @PageableDefault(size = 15) Pageable pageable) {

        try {
            List<LectureDTO.MyListResponseDTO> myListResponseDTOList = lectureService.getMyUploadList(Integer.parseInt(authentication.getPrincipal().toString()), pageable);
            return ResponseEntity.ok().body(myListResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 강의 정보 불러오기
    @GetMapping(value = "/unauth/view")
    @ApiOperation(value = "강의 정보 불러오기")
    public ResponseEntity<LectureDTO.ViewResponseDTO> getLectureInfo(LectureDTO.IdRequestDTO idRequestDTO) {

        try {
            LectureDTO.ViewResponseDTO viewResponseDTO = lectureService.getLectureInfo(idRequestDTO);
            return ResponseEntity.ok().body(viewResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

}