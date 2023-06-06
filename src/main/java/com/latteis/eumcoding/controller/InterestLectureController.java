package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.InterestLectureDTO;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.service.InterestLectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/heart")
@Api(tags = "Interest Lecture Controller", description = "관심 강의 컨트롤러")
public class InterestLectureController {

    private final InterestLectureService interestLectureService;


    // 강의 좋아요 추가
    @PostMapping(value = "/add")
    @ApiOperation(value = "강의 좋아요 추가")
    public ResponseEntity<Object> addHeart(@ApiIgnore Authentication authentication, @Valid @RequestBody InterestLectureDTO.IdRequestDTO idRequestDTO) {

        try {
            interestLectureService.addHeart(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 좋아요 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "강의 좋아요 삭제")
    public ResponseEntity<Object> deleteHeart(@ApiIgnore Authentication authentication, @Valid @RequestBody InterestLectureDTO.IdRequestDTO idRequestDTO) {

        try {
            interestLectureService.deleteHeart(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 강의 정보 불러오기
    @GetMapping(value = "/unauth/view")
    @ApiOperation(value = "해당 강의의 좋아요 수 가져오기")
    public ResponseEntity<InterestLectureDTO.ViewResponseDTO> getInterestCnt(LectureDTO.IdRequestDTO idRequestDTO) {

        try {
            InterestLectureDTO.ViewResponseDTO viewResponseDTO = interestLectureService.getInterestCnt(idRequestDTO);
            return ResponseEntity.ok().body(viewResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

}
