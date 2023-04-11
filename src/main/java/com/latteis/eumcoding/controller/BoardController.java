package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.service.BoardService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 글 작성
    @PostMapping(value = "/write")
    @ApiOperation(value = "게시판 글 작성")
    public ResponseEntity<Object> writeBoard(@RequestParam String memberId, @Valid @RequestBody BoardDTO.CreateRequestDTO createRequestDTO) {

        try {
            boardService.writeBoard(Integer.parseInt(memberId), createRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "게시판 글 수정")
    public ResponseEntity<Object> updateBoard(@RequestParam String memberId, @Valid @RequestBody BoardDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            boardService.updateBoard(Integer.parseInt(memberId), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "게시판 글 삭제")
    public ResponseEntity<Object> boardDelete(@RequestParam String memberId, @Valid @RequestBody BoardDTO.DeleteRequestDTO deleteRequestDTO) {

        try {
            boardService.deleteBoard(Integer.parseInt(memberId), deleteRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 목록
    @GetMapping(value = "/list")
    @ApiOperation(value = "게시판 글 목록 가져오기")
    public ResponseEntity<List<BoardDTO.ListResponseDTO>> getBoardList(@Valid @ModelAttribute BoardDTO.ListRequestDTO listRequestDTO) {

        try {
            List<BoardDTO.ListResponseDTO> listResponseDTOList = boardService.getBoardList(listRequestDTO);
            return ResponseEntity.ok().body(listResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 글 보기
    @GetMapping(value = "/view")
    @ApiOperation(value = "게시판 글 보기")
    public ResponseEntity<BoardDTO.ViewResponseDTO> viewBoard(@RequestParam int boardId) {

        try {
            BoardDTO.ViewResponseDTO viewResponseDTO= boardService.viewBoard(boardId);
            return ResponseEntity.ok().body(viewResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
