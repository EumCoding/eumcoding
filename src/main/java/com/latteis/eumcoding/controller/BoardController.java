package com.latteis.eumcoding.controller;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.service.BoardService;
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
@RequestMapping("/board")
@Api(tags = "Board Controller", description = "게시판 컨트롤러")
public class BoardController {

    private final BoardService boardService;

    // 글 작성
    @PostMapping(value = "/write")
    @ApiOperation(value = "게시판 글 작성")
    public ResponseEntity<Object> writeBoard(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardDTO.CreateRequestDTO createRequestDTO) {

        try {
            boardService.writeBoard(Integer.parseInt(authentication.getPrincipal().toString()), createRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 수정
    @PostMapping(value = "/update")
    @ApiOperation(value = "게시판 글 수정")
    public ResponseEntity<Object> updateBoard(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardDTO.UpdateRequestDTO updateRequestDTO) {

        try {
            boardService.updateBoard(Integer.parseInt(authentication.getPrincipal().toString()), updateRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 글 삭제
    @PostMapping(value = "/delete")
    @ApiOperation(value = "게시판 글 삭제")
    public ResponseEntity<Object> boardDelete(@ApiIgnore Authentication authentication, @Valid @RequestBody BoardDTO.IdRequestDTO idRequestDTO) {

        try {
            boardService.deleteBoard(Integer.parseInt(authentication.getPrincipal().toString()), idRequestDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    // 내가 작성한 글 목록 가져오기
    @GetMapping(value = "/mylist")
    @ApiOperation(value = "내가 작성한 글 목록 가져오기")
    public ResponseEntity<List<BoardDTO.MyListResponseDTO>> getMyBoardList(@ApiIgnore Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {
//    public ResponseEntity<List<BoardDTO.ListResponseDTO>> getMyBoardList(@ApiIgnore Authentication authentication, @Valid BoardDTO.ListRequestDTO listRequestDTO) {
        System.out.println(pageable + "aaaaaaaaaaaaa");
        try {
            List<BoardDTO.MyListResponseDTO> myListResponseDTOS = boardService.getMyBoardList(Integer.parseInt(authentication.getPrincipal().toString()), pageable);
            return ResponseEntity.ok().body(myListResponseDTOS);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 글 목록
    @GetMapping(value = "/unauth/list")
    @ApiOperation(value = "게시판 글 목록 가져오기")
    public ResponseEntity<List<BoardDTO.ListResponseDTO>> getBoardList(@Valid BoardDTO.ListRequestDTO listRequestDTO) {

        try {
            List<BoardDTO.ListResponseDTO> listResponseDTOList = boardService.getBoardList(listRequestDTO);
            return ResponseEntity.ok().body(listResponseDTOList);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }

    }

    // 글 보기
    @GetMapping(value = "/unauth/view")
    @ApiOperation(value = "게시판 글 보기")
    public ResponseEntity<BoardDTO.ViewResponseDTO> viewBoard(@Valid BoardDTO.IdRequestDTO idRequestDTO) {

        try {
            BoardDTO.ViewResponseDTO viewResponseDTO= boardService.viewBoard(idRequestDTO);
            System.out.println(viewResponseDTO + "성공");
            return ResponseEntity.ok().body(viewResponseDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
