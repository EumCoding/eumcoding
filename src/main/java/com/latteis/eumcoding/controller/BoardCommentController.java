package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.service.BoardCommentService;
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
@RequestMapping("/board/comment")
@Api(tags = "Board Comment Controller", description = "게시판 댓글 컨트롤러")
public class BoardCommentController {

    private final BoardCommentService boardCommentService;

    // 댓글 작성
    @PostMapping(value = "/write")
    @ApiOperation(value = "게시판 댓글 작성")
    public ResponseEntity<Object> writeComment(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardCommentDTO.WriteRequestDTO writeRequestDTO) {

        try {
            boardCommentService.writeComment(Integer.parseInt(authentication.getPrincipal().toString()), writeRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 대댓글 작성
    @PostMapping(value = "/write_reply")
    @ApiOperation(value = "게시판 대댓글 작성")
    public ResponseEntity<Object> writeReply(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardCommentDTO.WriteReplyRequestDTO writeReplyRequestDTO) {

        try {
            boardCommentService.writeReply(Integer.parseInt(authentication.getPrincipal().toString()), writeReplyRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 게시판 댓글 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "게시판 댓글 수정")
    public ResponseEntity<Object> updateComment(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardCommentDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            boardCommentService.updateComment(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 게시판 댓글 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "게시판 댓글 삭제")
    public ResponseEntity<Object> deleteComment(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardCommentDTO.IdRequestDTO idRequestDTO) {

        try {
            boardCommentService.deleteComment(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 게시판 댓글 가져오기
//    @GetMapping(value = "/list")
//    @ApiOperation(value = "게시판 댓글 리스트 가져오기")
//    public ResponseEntity<List<BoardCommentDTO.ListResponseDTO>> getCommentList(@Valid BoardDTO.IdRequestDTO idRequestDTO, @PageableDefault(size = 10) Pageable pageable) {
//
//        try {
//            log.info("controller");
//            List<BoardCommentDTO.ListResponseDTO> listResponseDTOS = boardCommentService.getCommentList(idRequestDTO, pageable);
//            return ResponseEntity.ok().body(listResponseDTOS);
//        } catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//
//    }

}
