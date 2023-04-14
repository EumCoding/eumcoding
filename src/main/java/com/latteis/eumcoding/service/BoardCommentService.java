package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.BoardComment;
import com.latteis.eumcoding.persistence.BoardCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;

    // 댓글 작성
    public void writeComment(int memberId, BoardCommentDTO.WriteRequestDTO writeRequestDTO) {

        try {

            BoardComment boardComment = BoardComment.builder()
                    .memberId(memberId)
                    .boardId(writeRequestDTO.getBoardId())
                    .content(writeRequestDTO.getContent())
                    .commentDay(LocalDateTime.now())
                    .step(0)
                    .modified(0)
                    .build();
            boardComment.setGroupNum(boardCommentRepository.save(boardComment).getId());
            boardCommentRepository.save(boardComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.writeComment() : 에러 발생");
        }
    }

    // 대댓글 작성
    public void writeReply(int memberId, BoardCommentDTO.WriteReplyRequestDTO writeReplyRequestDTO) {

        try {

            BoardComment boardComment = BoardComment.builder()
                    .memberId(memberId)
                    .boardId(writeReplyRequestDTO.getBoardId())
                    .content(writeReplyRequestDTO.getContent())
                    .commentDay(LocalDateTime.now())
                    .step(writeReplyRequestDTO.getStep() + 1)
                    .groupNum(writeReplyRequestDTO.getId())
                    .modified(0)
                    .build();
            boardCommentRepository.save(boardComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.writeReply() : 에러 발생");
        }
    }

    // 댓글 수정
    public void updateComment(int memberId, BoardCommentDTO.UpdateRequestDTO updateRequestDTO) {

        try {

            BoardComment boardComment = boardCommentRepository.findByIdAndMemberId(updateRequestDTO.getId(), memberId);
            boardComment.setContent(updateRequestDTO.getContent());
            boardComment.setModified(1);
            boardCommentRepository.save(boardComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.updateComment() : 에러 발생");
        }
    }

    // 댓글 삭제
    public void deleteComment(int memberId, BoardCommentDTO.IdRequestDTO idRequestDTO) {

        try {

            // 삭제할 최상위 댓글 엔티티 가져옴
            BoardComment topBoardComment = boardCommentRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
            // 현재 단계에서 하위 단계로 내려갈 때 이곳에 백업해둠
            List<BoardComment> oneStepUp = new ArrayList<>();
            // 현재 단계의 댓글들을 담고 있는 변수
            List<BoardComment> boardComments = new ArrayList<>();

            // 삭제할 최상위 댓글의 대댓글들이 모두 없어지면 false 반환으로 while문 종료
            while (boardCommentRepository.existsByGroupNum(topBoardComment.getId())) {
                // boardComments엔 값이 없고 oneStepup에는 값이 있다면
                if (boardComments.isEmpty() && !oneStepUp.isEmpty()) {
                        // 값을 받고 oneStepUp은 비운다
                        boardComments = oneStepUp;
                        oneStepUp.clear();
                }
                // boardComments와 oneStepUp 둘 다 값이 없었다면
                if (boardComments.isEmpty()){
                    // 최상위 댓글의 한 단계 아래의 댓글들을 가져온다
                    boardComments = boardCommentRepository.findAllByGroupNum(topBoardComment.getId());
                }
                // foreach문 안에서 삭제를 완료했다면 0을 할당
                int del = 1;
                // boardComments 리스트에 담긴 댓글 수만큼 반복한다
                for (BoardComment boardComment : boardComments) {
                    // 현재 댓글의 하위 단계 댓글이 있는지 확인하기 위해 하위 단계 리스트 가져옴
                    List<BoardComment> boardComments1 = boardCommentRepository.findAllByGroupNum(boardComment.getId());
                    // 현재 댓글의 하위 단계 댓글이 있다면
                    if (!boardComments1.isEmpty()) {
                        // oneStepUp에 현 단계 댓글 리스트를 백업한다
                        oneStepUp = boardComments;
                        // 하위 단계 댓글을 할당받음
                        boardComments = boardComments1;
                        // 삭제 작업 안 했으므로 1
                        del = 1;
                        break;
                    }
                    // 현재 댓글의 한위 단계 댓글이 없으므로 삭제 작업 진행
                    boardCommentRepository.delete(boardComment);
                    // 삭제 작업 진행하므로 0
                    del = 0;
                }
                // del 값이 0이면
                if (del == 0) {
                    // 삭제 완료한 리스트를 비워준다
                    boardComments.clear();
                }
            }

            // 삭제할 최상위 댓글의 하위 단계 댓글을 모두 삭제했으므로 최상위 댓글 삭제
            boardCommentRepository.delete(topBoardComment);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.deleteComment() : 에러 발생");
        }
    }

    // 해당 게시글의 모든 댓글 삭제
    public void deleteAllComment(int boardId) {

        try {
            List<BoardComment> boardComments = boardCommentRepository.findAllByBoardId(boardId);
            boardCommentRepository.deleteAll(boardComments);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.deleteAllComment() : 에러 발생");
        }

    }

    // 댓글 리스트 가져오기
    public List<BoardCommentDTO.ListResponseDTO> getCommentList(BoardDTO.IdRequestDTO idRequestDTO, Pageable pageable) {

        try {

            // 해당 댓글의 답글이 있으면 true
            boolean existReply;
            // 엔티티 리스트에 담기
            Page<Object[]> pages = boardCommentRepository.getCommentList(idRequestDTO.getId(), 0, pageable);
            List<Object[]> objects = pages.getContent();
            List<BoardCommentDTO.ListResponseDTO> listResponseDTOS = new ArrayList<>();
            // 반복문으로 DTO 리스트에 넣기
            for (Object[] object : objects) {
                // DTO에 담기
                BoardCommentDTO.ListResponseDTO listResponseDTO = new BoardCommentDTO.ListResponseDTO(object);
                // 해당 댓글에 답글이 있는지 검사
                existReply = boardCommentRepository.existsByStepAndGroupNum(1, listResponseDTO.getId());
                // 검사한 값 저장
                listResponseDTO.setExistReply(existReply);
                listResponseDTOS.add(listResponseDTO);
            }
            return listResponseDTOS;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.getCommentList() : 에러 발생");
        }
    }

    // 대댓글 리스트 가져오기
    public List<BoardCommentDTO.ListResponseDTO> getReplyList(BoardCommentDTO.IdRequestDTO idRequestDTO, Pageable pageable) {

        try {

            // 해당 댓글의 답글이 있으면 true
            boolean existReply;
            // 댓글 아이디
            int id = idRequestDTO.getId();
            // 댓글의 step 가져옴
            int step = boardCommentRepository.getStepById(id);

            // 엔티티 리스트에 담기. 현재 댓글의 답글 step으로 찾아야 하므로 step + 1
            Page<Object[]> pages = boardCommentRepository.getReplyList(id, step + 1, pageable);
            List<Object[]> objects = pages.getContent();
            List<BoardCommentDTO.ListResponseDTO> listResponseDTOS = new ArrayList<>();
            // 반복문으로 DTO 리스트에 넣기
            for (Object[] object : objects) {
                // DTO에 담기
                BoardCommentDTO.ListResponseDTO listResponseDTO = new BoardCommentDTO.ListResponseDTO(object);
                // 해당 답글에 답글이 있는지 검사. 답글의 답글을 검사해야 하기 때문에 step + 2
                existReply = boardCommentRepository.existsByStepAndGroupNum(step + 2, listResponseDTO.getId());
                // 검사한 값 저장
                listResponseDTO.setExistReply(existReply);
                listResponseDTOS.add(listResponseDTO);
            }
            return listResponseDTOS;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.getReplyList() : 에러 발생");
        }
    }

    //  내가 작성한 게시판 댓글 리스트 가져오기
    public List<BoardCommentDTO.MyListResponseDTO> getMyCommentList(int memberId, Pageable pageable) {

        try {

            // 엔티티 리스트에 담기. 현재 댓글의 답글 step으로 찾아야 하므로 step + 1
            Page<Object[]> pages = boardCommentRepository.getMyList(memberId, pageable);
            List<Object[]> objects = pages.getContent();
            List<BoardCommentDTO.MyListResponseDTO> myListResponseDTOS = new ArrayList<>();
            // 반복문으로 DTO 리스트에 넣기
            for (Object[] object : objects) {
                // DTO에 담기
                BoardCommentDTO.MyListResponseDTO myListDTO = new BoardCommentDTO.MyListResponseDTO(object);
                myListResponseDTOS.add(myListDTO);
            }
            return myListResponseDTOS;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.getMyCommentList() : 에러 발생");
        }
    }

}
