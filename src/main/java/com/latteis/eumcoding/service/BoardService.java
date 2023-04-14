package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.persistence.BoardRepository;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@ApiOperation(value = "게시물 보기 응답 DTO")
public class BoardService {

    private final BoardRepository boardRepository;

    private final BoardCommentService boardCommentService;

    private final EntityManager em;

    // 글 작성
    public void writeBoard(int memberId, BoardDTO.CreateRequestDTO createRequestDTO) {

        try {
            // Board 엔티티 생성
            Board board = Board.builder()
                    .memberId(memberId)
                    .title(createRequestDTO.getTitle())
                    .content(createRequestDTO.getContent())
                    .createdDay(LocalDateTime.now())
                    .type(createRequestDTO.getType())
                    .views(0)
                    .build();
            // 엔티티 저장
            boardRepository.save(board);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardService.writeBoard() : 에러 발생");
        }

    }

    // 글 수정
    public void updateBoard(int memberId, BoardDTO.UpdateRequestDTO updateRequestDTO) {

        try {

            // 받아온 BoardDTO의 Id에 맞는 엔티티 가져옴
            Board board = boardRepository.findByIdAndMemberId(updateRequestDTO.getBoardId(), memberId);
            // 수정 값 입력
            board.setTitle(updateRequestDTO.getTitle());
            board.setContent(updateRequestDTO.getContent());
            board.setUpdatedDay(LocalDateTime.now());
            // 저장
            boardRepository.save(board);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardService.updateBoard() : 에러 발생");
        }

    }

    // 글 삭제
    public void deleteBoard(int memberId, BoardDTO.IdRequestDTO idRequestDTO) {

        try {

            // 받아온 BoardDTO의 Id에 맞는 엔티티 가져옴
            Board board = boardRepository.findByIdAndMemberId(idRequestDTO.getId(), memberId);
            // 삭제
            boardRepository.delete(board);
            // 해당 게시물의 모든 댓글 삭제
            boardCommentService.deleteAllComment(board.getId());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardService.deleteBoard() : 에러 발생");
        }

    }

    // 내가 쓴 글 목록
    public List<BoardDTO.MyListResponseDTO> getMyBoardList(int memberId, Pageable pageable) {

        try {
            // 페이징으로 엔티티 리스트 가져오기
            Page<Board> boardPage = boardRepository.findAllByMemberIdOrderByCreatedDayDesc(memberId, pageable);
            // 엔티티 리스트에 담기
            List<Board> boardList = boardPage.getContent();
            List<BoardDTO.MyListResponseDTO> myListResponseDTOS = new ArrayList<>();

            for (Board board : boardList) {
                BoardDTO.MyListResponseDTO myListResponseDTO = new BoardDTO.MyListResponseDTO();
                myListResponseDTO.setId(board.getId());
                myListResponseDTO.setTitle(board.getTitle());
                myListResponseDTO.setViews(board.getViews());
                myListResponseDTO.setCreatedDay(board.getCreatedDay());
                myListResponseDTOS.add(myListResponseDTO);
            }
            return myListResponseDTOS;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardService.getMyBoardList() : 에러 발생");
        }
    }

    // 글 목록 가져오기
    public List<BoardDTO.ListResponseDTO> getBoardList(BoardDTO.ListRequestDTO listRequestDTO) {

        try {

            // 정렬 문자열을 담는 변수
            String sort = null;
            // 한 페이지당 몇 개의 개시물을 보여줄 지
            int pageSize = listRequestDTO.getPageSize();
            // 페이지 번호
            int page = listRequestDTO.getPage() * listRequestDTO.getPageSize();

            // 정렬 기준
            switch (listRequestDTO.getSort()) {
                // 날짜
                case 0 :
                    sort = "created_day";
                    break;
                //좋아요
                case 1 :
                    sort = "count(ib.board_id)";
                    break;
                //조회수
                case 2 :
                    sort = "b.views";
                    break;
            }

            // StringBuffer에 쿼리 작성
            StringBuffer sb = new StringBuffer();
            sb.append("select b.id, b.member_id, b.title, b.views, m.nickname, b.created_day, count(ib.board_id) as heart ");
            sb.append("from board b left join interest_board ib on b.id = ib.board_id, member m ");
            sb.append("where m.id = b.member_id and b.type = ? ");
            sb.append("group by b.id ");
            sb.append("order by " + sort + " desc ");
            sb.append("limit ?, ?");

            // 쿼리 생성
            Query query = em.createNativeQuery(sb.toString())
                    .setParameter(1, listRequestDTO.getType())
                    .setParameter(2, page)
                    .setParameter(3, pageSize);

            // DTO에 담기
            List<Object[]> results = query.getResultList();
            List<BoardDTO.ListResponseDTO> listResponseDTO = results.stream()
                    .map(o -> new BoardDTO.ListResponseDTO(o))
                    .collect(Collectors.toList());

            return listResponseDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardService.getBoardList() : 에러 발생");
        }

    }

    // 글 보기
    public BoardDTO.ViewResponseDTO viewBoard(BoardDTO.IdRequestDTO idRequestDTO) {

        try {

            // StringBuffer에 쿼리 작성
            StringBuffer sb = new StringBuffer();
            sb.append("select b.id, b.member_id, b.title, b.content, b.views, b.created_day, m.nickname, count(ib.board_id) as heart ");
            sb.append("from board b left join interest_board ib on b.id = ib.board_id, member m ");
            sb.append("where m.id = b.member_id and b.id = ?");

            // 쿼리 생성
            Query query = em.createNativeQuery(sb.toString())
                    .setParameter(1, idRequestDTO.getId());

            // DTO에 담기
            Object[] results = (Object[]) query.getResultList().get(0);
            BoardDTO.ViewResponseDTO viewResponseDTO = new BoardDTO.ViewResponseDTO(results);

            return viewResponseDTO;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardService.viewBoard() : 에러 발생");
        }

    }

}
