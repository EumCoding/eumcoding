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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardCommentRepository boardCommentRepository;

//    EntityManager entityManager;

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

            BoardComment topBoardComment = boardCommentRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);

            //=========== while문으로 감싼다고 생각==============
            // exist 사용해보기
//            List<BoardComment> boardComments = boardCommentRepository.findAllByGroupNum(topBoardComment.getId());
            List<BoardComment> boardComments = new ArrayList<>();
            // 리스트가 비어있다면 상위 댓글이나 같은 레벨의 댓글 가져옴. 아직 구현 못 함
            if(boardComments == null) boardComments = boardCommentRepository.findAllByGroupNum(topBoardComment.getId());
            // 리스트 for문
            for (BoardComment boardComment : boardComments) {
                // 해당 댓글의 대댓글이 있는지 확인하기 위해 대댓글 리스트 가져옴
                List<BoardComment> boardComments1 = boardCommentRepository.findAllByGroupNum(boardComment.getId());
                // 리스트에 값이 있다면 현재 for문을 해당 리스트로 교체
                // 교체 후 break를 하지 않아도 교체가 되는지 테스트(테스트코드작성)
                if (boardComments1 != null) {
                    boardComments = boardComments1;
                    break;
                }
                // 리스트가 비어있다면 해당 댓글 삭제
                boardCommentRepository.delete(boardComment);
                // 댓글 삭제 후 리스트에서 삭제
                boardComments.remove(boardComment);
            }
            // ============while문=============

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
//    public List<BoardCommentDTO.ListResponseDTO> getCommentList(BoardDTO.IdRequestDTO idRequestDTO, Pageable pageable) {
//
//        try {
//            // 페이지 갯수에 맞게 오래된 순으로 가져오기
////            Page<BoardComment> boardCommentPage = boardCommentRepository.findAllByBoardIdOrderByCommentDay(idRequestDTO.getId(), pageable);
//            // 엔티티 리스트에 담기
////            List<BoardComment> boardComments = boardCommentPage.getContent();
////            List<BoardCommentDTO.ListResponseDTO> listResponseDTOS = new ArrayList<>();
//            log.info("service");
//            List<Object[]> list = boardCommentRepository.findAllByBoardIdOrderByCommentDay(idRequestDTO.getId());
//            List<BoardCommentDTO.ListResponseDTO> listResponseDTOS = new ArrayList<>();
//            // 반복문으로 DTO 리스트에 넣기
//            for (Object object : list) {
//                BoardCommentDTO.ListResponseDTO listResponseDTO = new BoardCommentDTO.ListResponseDTO(object);
//                listResponseDTOS.add(listResponseDTO);
//            }
//            return listResponseDTOS;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException("BoardCommentService.getCommentList() : 에러 발생");
//        }
//    }

}
