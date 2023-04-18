package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.ReviewCommentDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.service.ReviewCommentService;
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
@RequestMapping("/lecture/review/comment")
@Api(tags = "Review Comment Controller", description = "리뷰 댓글 컨트롤러")
public class ReviewCommentController {

    private final ReviewCommentService reviewCommentService;

    // 리뷰 댓글 작성
    @PostMapping(value = "/write")
    @ApiOperation(value = "리뷰 댓글 작성")
    public ResponseEntity<Object> writeReviewComment(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewCommentDTO.WriteRequestDTO writeRequestDTO) {

        try {
            reviewCommentService.writeReviewComment(Integer.parseInt(authentication.getPrincipal().toString()), writeRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 리뷰 댓글 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "리뷰 댓글 수정")
    public ResponseEntity<Object> updateReviewComment(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewCommentDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            reviewCommentService.updateReviewComment(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 리뷰 댓글 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "리뷰 댓글 삭제")
    public ResponseEntity<Object> deleteReviewComment(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewCommentDTO.IdRequestDTO idRequestDTO) {

        try {
            reviewCommentService.deleteReviewComment(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

}
