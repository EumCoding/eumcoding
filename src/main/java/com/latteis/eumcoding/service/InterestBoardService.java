package com.latteis.eumcoding.service;

import com.latteis.eumcoding.model.Board;
import com.latteis.eumcoding.model.InterestBoard;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.BoardRepository;
import com.latteis.eumcoding.persistence.InterestBoardRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterestBoardService {

    private final InterestBoardRepository interestBoardRepository;

    private final MemberRepository memberRepository;

    private  final BoardRepository boardRepository;


    // 좋아요 추가
    public void addHeart(int memberId, int boardId) {

        InterestBoard interestBoard = InterestBoard.builder()
                .memberId(memberId)
                .boardId(boardId)
                .build();

        interestBoardRepository.save(interestBoard);

    }

    // 게시글 좋아요 삭제
    public void deleteHeart(int memberId, int boardId) {

        InterestBoard interestBoard = interestBoardRepository.findByBoardIdAndMemberId(boardId, memberId);
        interestBoardRepository.delete(interestBoard);

    }
}
