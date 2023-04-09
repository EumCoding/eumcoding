package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.BoardRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    // 글 작성
    public void boardWrite(int memberId, BoardDTO.CreateRequestDTO createRequestDTO) {

        // Board 엔티티 생성
        Board board = new Board();
//        Member member = memberRepository.findByMemberId(memberId);
//        board.setMember(member);
        board.setTitle(createRequestDTO.getTitle());
        board.setContent(createRequestDTO.getContent());
        board.setCreated_day(LocalDateTime.now());
        board.setType(createRequestDTO.getType());
        board.setViews(0);
        // 엔티티 저장
        boardRepository.save(board);

    }

    // 글 수정
    public void boardUpdate(int memberId, BoardDTO.UpdateRequestDTO updateRequestDTO) {

        // 로그인된 유저와 글 작성자 비교 검증
        if(memberId != updateRequestDTO.getMemberId()) {
            log.warn("BoardService.boardUpdate() : 로그인된 유저와 글 작성자가 다릅니다.");
            throw new RuntimeException("BoardService.boardUpdate() : 로그인된 유저와 글 작성자가 다릅니다.");
        }

        // 받아온 BoardDTO의 Id에 맞는 엔티티 가져옴
        Board board = boardRepository.findById(updateRequestDTO.getBoardId());
        // 수정 값 입력
        board.setTitle(updateRequestDTO.getTitle());
        board.setContent(updateRequestDTO.getContent());
        board.setUpdated_day(LocalDateTime.now());
        // 저장
        boardRepository.save(board);

    }

    // 글 삭제
    public void boardDelete(int memberId, BoardDTO.DeleteRequestDTO deleteRequestDTO) {

        // 로그인된 유저와 글 작성자 비교 검증
        if(memberId != deleteRequestDTO.getMemberId()) {
            log.warn("BoardService.boardUpdate() : 로그인된 유저와 글 작성자가 다릅니다.");
            throw new RuntimeException("BoardService.boardUpdate() : 로그인된 유저와 글 작성자가 다릅니다.");
        }

        // 받아온 BoardDTO의 Id에 맞는 엔티티 가져옴
        Board board = boardRepository.findById(deleteRequestDTO.getBoardId());
        // 삭제
        boardRepository.delete(board);

    }

}
