package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.service.LectureService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

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
    public ResponseEntity<Object> createLecture(@ApiIgnore Authentication authentication, @Valid @RequestBody LectureDTO.CreateRequestDTO createRequestDTO) {

        try {
            lectureService.createLecture(Integer.parseInt(authentication.getPrincipal().toString()), createRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}