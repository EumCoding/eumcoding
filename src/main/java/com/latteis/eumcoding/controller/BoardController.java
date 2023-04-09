package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 글 작성
    @PostMapping(value = "/write")
    public ResponseEntity<Object> boardWrite(@RequestParam String memberId, @Valid @RequestBody BoardDTO.CreateRequestDTO createRequestDTO) {

        try {
            boardService.boardWrite(Integer.parseInt(memberId), createRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 수정
    @PostMapping(value = "/update")
    public ResponseEntity<Object> boardUpdate(@RequestParam String memberId, @Valid @RequestBody BoardDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            boardService.boardUpdate(Integer.parseInt(memberId), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 삭제
    @PostMapping(value = "/delete")
    public ResponseEntity<Object> boardDelete(@RequestParam String memberId, @Valid @RequestBody BoardDTO.DeleteRequestDTO deleteRequestDTO) {

        try {
            boardService.boardDelete(Integer.parseInt(memberId), deleteRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
