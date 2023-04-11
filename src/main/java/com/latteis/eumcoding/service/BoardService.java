package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.BoardDTO;
import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.persistence.BoardRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final EntityManager em;

    // 글 작성
    public void writeBoard(int memberId, BoardDTO.CreateRequestDTO createRequestDTO) {

        // Board 엔티티 생성
        Board board = Board.builder()
                .title(createRequestDTO.getTitle())
                .content(createRequestDTO.getContent())
                .created_day(LocalDateTime.now())
                .type(createRequestDTO.getType())
                .views(0)
                .build();
//        Member member = memberRepository.findByMemberId(memberId);
//        board.setMember(member);
        // 엔티티 저장
        boardRepository.save(board);

    }

    // 글 수정
    public void updateBoard(int memberId, BoardDTO.UpdateRequestDTO updateRequestDTO) {

        // 로그인된 유저와 글 작성자 비교 검증
        if (memberId != updateRequestDTO.getMemberId()) {
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
    public void deleteBoard(int memberId, BoardDTO.DeleteRequestDTO deleteRequestDTO) {

        // 로그인된 유저와 글 작성자 비교 검증
        if (memberId != deleteRequestDTO.getMemberId()) {
            log.warn("BoardService.boardUpdate() : 로그인된 유저와 글 작성자가 다릅니다.");
            throw new RuntimeException("BoardService.boardUpdate() : 로그인된 유저와 글 작성자가 다릅니다.");
        }

        // 받아온 BoardDTO의 Id에 맞는 엔티티 가져옴
        Board board = boardRepository.findById(deleteRequestDTO.getBoardId());
        // 삭제
        boardRepository.delete(board);

    }

    // 글 목록 가져오기
    public List<BoardDTO.ListResponseDTO> getBoardList(BoardDTO.ListRequestDTO listRequestDTO) {

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
        List<BoardDTO.ListResponseDTO> listResponseDTOPage = results.stream()
                .map(o -> new BoardDTO.ListResponseDTO(o))
                .collect(Collectors.toList());

        return listResponseDTOPage;
    }

    // 글 보기
    public BoardDTO.ViewResponseDTO viewBoard(int boardId) {

        // StringBuffer에 쿼리 작성
        StringBuffer sb = new StringBuffer();
        sb.append("select b.id, b.member_id, b.title, b.content, b.views, b.created_day, m.nickname, count(ib.board_id) as heart ");
        sb.append("from board b left join interest_board ib on b.id = ib.board_id, member m ");
        sb.append("where m.id = b.member_id and b.id = ?");

        // 쿼리 생성
        Query query = em.createNativeQuery(sb.toString())
                .setParameter(1, boardId);

        // DTO에 담기
        Object[] results = (Object[]) query.getResultList().get(0);
        BoardDTO.ViewResponseDTO viewResponseDTO = new BoardDTO.ViewResponseDTO(results);

        return viewResponseDTO;

    }

}
