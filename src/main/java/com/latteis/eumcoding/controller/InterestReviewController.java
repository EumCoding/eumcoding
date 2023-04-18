package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.InterestReviewDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.service.InterestReviewService;
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
@RequestMapping("/lecture/review/heart")
@Api(tags = "Interest Review Controller", description = "리뷰 좋아요 컨트롤러")
public class InterestReviewController {

    private final InterestReviewService interestReviewService;

    // 리뷰 좋아요 추가
    @PostMapping(value = "/add")
    @ApiOperation(value = "리뷰 좋아요 추가")
    public ResponseEntity<Object> addHeart(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewDTO.IdRequestDTO idRequestDTO) {

        try {
            interestReviewService.addHeart(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 리뷰 좋아요 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "리뷰 좋아요 삭제")
    public ResponseEntity<Object> deleteHeart(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewDTO.IdRequestDTO idRequestDTO) {

        try {
            interestReviewService.deleteHeart(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
