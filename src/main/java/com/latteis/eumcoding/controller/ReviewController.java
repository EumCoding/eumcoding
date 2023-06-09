package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.service.ReviewService;
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
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/review")
@Api(tags = "Review Controller", description = "리뷰 컨트롤러")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping(value = "/write")
    @ApiOperation(value = "리뷰 작성")
    public ResponseEntity<Object> writeReview(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewDTO.WriteRequestDTO writeRequestDTO) {

        try {
            reviewService.writeReview(Integer.parseInt(authentication.getPrincipal().toString()), writeRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 리뷰 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "리뷰 수정")
    public ResponseEntity<Object> updateReview(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewDTO.UpdateRequestDTO updateRequestDTO) {


        try {
            reviewService.updateReview(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 리뷰 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "리뷰 삭제")
    public ResponseEntity<Object> deleteReview(@ApiIgnore Authentication authentication, @Valid @RequestBody ReviewDTO.IdRequestDTO idRequestDTO) {


        try {
            reviewService.deleteReview(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 리뷰 목록
    @GetMapping(value = "/unauth/list")
    @ApiOperation(value = "리뷰 목록")
    public ResponseEntity<List<ReviewDTO.ListResponseDTO>> getReviewList(@PageableDefault(size = 10) Pageable pageable, @Valid LectureDTO.IdRequestDTO idRequestDTO) {

        try {
            List<ReviewDTO.ListResponseDTO> listResponseDTOList = reviewService.getReviewList(pageable, idRequestDTO);
            return ResponseEntity.ok().body(listResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 내가 쓴 리뷰 목록
    @PostMapping(value = "/my_list")
    @ApiOperation(value = "내가 쓴 리뷰 목록")
    public ResponseEntity<List<ReviewDTO.MyListResponseDTO>> getMyReviewList(@ApiIgnore Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {

        try {
            List<ReviewDTO.MyListResponseDTO> myListResponseDTOList = reviewService.getMyReviewList(Integer.parseInt(authentication.getPrincipal().toString()), pageable);
            return ResponseEntity.ok().body(myListResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }
}
