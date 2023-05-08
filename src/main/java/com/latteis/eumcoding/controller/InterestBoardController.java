package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.service.InterestBoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/interest_board")
@Api(tags = "Interest Board Controller", description = "게시판 좋아요 컨트롤러")
public class InterestBoardController {

    private final InterestBoardService interestBoardService;

    // 게시글 좋아요 추가
    @PostMapping(value = "/add")
    @ApiOperation(value = "게시글 좋아요 추가")
    public ResponseEntity<Object> addHeart(@ApiIgnore Authentication authentication, @RequestParam int boardId) {

        try {
            interestBoardService.addHeart(Integer.parseInt(authentication.getPrincipal().toString()), boardId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 게시글 좋아요 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "게시글 좋아요 삭제")
    public ResponseEntity<Object> deleteHeart(@ApiIgnore Authentication authentication, @RequestParam int boardId) {

        try {
            interestBoardService.deleteHeart(Integer.parseInt(authentication.getPrincipal().toString()), boardId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
