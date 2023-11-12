package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.LectureDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.dto.TeacherListReviewDTO;
import com.latteis.eumcoding.service.ReviewService;
import com.latteis.eumcoding.service.TeacherQuestionAndReviewListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture/review")
@Api(tags = "Review Controller", description = "리뷰 컨트롤러")
public class ReviewController {

    private final ReviewService reviewService;
    private final TeacherQuestionAndReviewListService teacherQuestionAndReviewListService;

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

    // 내가 쓴 리뷰 목록 + 날짜로 검색
    @PostMapping(value = "/my_list")
    @ApiOperation(value = "내가 쓴 리뷰 목록")
    public ResponseEntity<List<ReviewDTO.MyListResponseDTO>> getMyReviewList(@ApiIgnore Authentication authentication, @PageableDefault(size = 10) Pageable pageable, @Valid @RequestBody ReviewDTO.MyListRequestDTO myListRequestDTO) {

        try {
            List<ReviewDTO.MyListResponseDTO> myListResponseDTOList = reviewService.getMyReviewList(Integer.parseInt(authentication.getPrincipal().toString()), pageable, myListRequestDTO);
            return ResponseEntity.ok().body(myListResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

    }


    @ApiOperation("나에 대한 리뷰 목록 조회")
    @GetMapping("/teacher/list")
    public ResponseEntity<TeacherListReviewDTO> getReviewList(@ApiIgnore Authentication authentication,
                                                              @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                                              @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                                              @RequestParam(required = false) Integer lectureId,
                                                              @RequestParam(name = "page", defaultValue = "1") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size)
    {
        int adminId = Integer.parseInt(authentication.getPrincipal().toString());
        // 나에대한 리뷰 목록 조회
        TeacherListReviewDTO reviewDTO = teacherQuestionAndReviewListService.getMyLectureReviews(adminId,start,end,lectureId,page,size);
        return ResponseEntity.ok(reviewDTO);
    }
}
