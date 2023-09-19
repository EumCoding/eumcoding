package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.AnswerDTO;
import com.latteis.eumcoding.dto.BoardCommentDTO;
import com.latteis.eumcoding.dto.QuestionCommentDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Question;
import com.latteis.eumcoding.model.QuestionComment;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionCommentService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final QuestionCommentRepository questionCommentRepository;

    public QuestionCommentDTO.WriteRequestDTO writeComment(int memberId, QuestionCommentDTO.WriteRequestDTO questionCommentWriteDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));


        // 강좌에 대한 멤버 정보.(해당 멤버가 강좌를 만들었는지.)
        Question question = questionRepository.findById(questionCommentWriteDTO.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));
        Lecture lecture = question.getLecture();

        //글 작성자도 댓글 달 수있게 허용해야함
        boolean isQuestionAuthor = question.getMember().getId() == memberId;


        if (lecture.getMember().getId() != memberId && !isQuestionAuthor) {
            throw new IllegalArgumentException("강사 혹은 작성자만이 댓글을 작성할 수 있습니다.");
        }

/*        // 질문에 대한 답이 있는지 체크
        boolean completeAnswer = questionCommentRepository.existsByQuestion(question.getId());
        if (completeAnswer) {
            throw new IllegalStateException("답변을 하였습니다.");
        }*/


         QuestionComment questionComment= QuestionComment.builder()
                .question(question)
                .member(member)
                .content(questionCommentWriteDTO.getContent())
                .createdDay(LocalDateTime.now())
                .step(0)
                .build();

        questionCommentRepository.save(questionComment);

        QuestionCommentDTO.WriteRequestDTO QuestionWriteDTOResult = QuestionCommentDTO.WriteRequestDTO.builder()
                .questionId(questionComment.getQuestion().getId())
                .content(questionComment.getContent())
                .build();

        return QuestionWriteDTOResult;
    }

    //대댓글달기
    public QuestionCommentDTO.WriteReplyRequestDTO writeReply(int memberId, QuestionCommentDTO.WriteReplyRequestDTO WriteReplyRequestDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));

        // 강좌에 대한 멤버 정보.(해당 멤버가 강좌를 만들었는지.)
        Question question = questionRepository.findById(WriteReplyRequestDTO.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));
        Lecture lecture = question.getLecture();

        boolean isQuestionAuthor = question.getMember().getId() == memberId;
        if (!isQuestionAuthor && lecture.getMember().getId() != memberId) {
            throw new IllegalArgumentException("작성자 및 강사 외에는 댓글을 달 수 없습니다.");
        }

/*
        // 질문에 대한 답이 있는지 체크
        boolean completeAnswer = questionCommentRepository.existsByQuestion(question.getId());
        if (completeAnswer) {
            throw new IllegalStateException("강사가 답변을 한 질문입니다.");
        }
*/


        QuestionComment questionComment= QuestionComment.builder()
                .question(question)
                .member(member)
                .content(WriteReplyRequestDTO.getContent())
                .createdDay(LocalDateTime.now())
                .updatedDay(LocalDateTime.now())
                .groupNum(WriteReplyRequestDTO.getId())
                .step(WriteReplyRequestDTO.getStep() + 1)
                .build();

        questionCommentRepository.save(questionComment);

        QuestionCommentDTO.WriteReplyRequestDTO questionWriteReplyDTOResult = QuestionCommentDTO.WriteReplyRequestDTO.builder()
                .questionId(questionComment.getQuestion().getId())
                .content(questionComment.getContent())
                .step(WriteReplyRequestDTO.getStep() + 1)
                .build();

        return questionWriteReplyDTOResult;
    }

  public QuestionCommentDTO.updateRequestDTO updateComment(int memberId,int questionCommentId,QuestionCommentDTO.updateRequestDTO updateRequestDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));


        QuestionComment questionComment = questionCommentRepository.findById(questionCommentId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        // 자기가 작성한 답변인지 체크
        if (questionComment.getMember().getId() != memberId) {
            throw new IllegalArgumentException("자신이 작성한 답변만 수정할 수 있습니다.");
        }

        // 수정된 답변+날짜
      questionComment.setContent(updateRequestDTO.getContent());
      questionComment.setUpdatedDay(LocalDateTime.now());

      questionCommentRepository.save(questionComment);

      QuestionCommentDTO.updateRequestDTO questionCommentUpdateDTOResult = QuestionCommentDTO.updateRequestDTO.builder()
                .questionCommentId(questionComment.getId())
                .content(questionComment.getContent())
                .build();

        return questionCommentUpdateDTOResult;
    }

/*
해당 메서드는 최상위 댓글이든, 대댓글이든 지우면 그 밑에 댓글들은 삭제되는게아님
    public QuestionCommentDTO.deleteRequestDTO deleteComment(int memberId, int questionCommentId) {

        QuestionComment questionComment = questionCommentRepository.findById(questionCommentId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        // 자기가 작성한 답변인지 체크
        if (questionComment.getMember().getId() != memberId) {
            throw new IllegalArgumentException("자신의 댓글만 삭제 할 수 있습니다.");
        }

        questionCommentRepository.delete(questionComment);

        QuestionCommentDTO.deleteRequestDTO questionCommentDeleteDTOResult = QuestionCommentDTO.deleteRequestDTO.builder()
                .questionCommentId(questionCommentId)
                .build();

        return questionCommentDeleteDTOResult;
    }*/

    public void deleteComment(int memberId, QuestionCommentDTO.deleteRequestDTO deleteRequestDTO) {

        try {
            QuestionComment parentQuestionComment = questionCommentRepository.findById(deleteRequestDTO.getQuestionCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

            // 자기가 작성한 답변인지 체크
            if (parentQuestionComment.getMember().getId() != memberId) {
                throw new IllegalArgumentException("자신의 댓글만 삭제 할 수 있습니다.");
            }

            int parentStep = parentQuestionComment.getStep();
            List<QuestionComment> oneStepUp = new ArrayList<>();
            List<QuestionComment> questionComments = new ArrayList<>();

            // 동일한 로직으로 대댓글을 검사하고 삭제합니다.
            while (questionCommentRepository.existsByGroupNumAndStep(parentQuestionComment.getId(), parentStep + 1)) {
                if (questionComments.isEmpty() && !oneStepUp.isEmpty()) {
                    questionComments = oneStepUp;
                    oneStepUp.clear();
                }
                if (questionComments.isEmpty()) {
                    questionComments = questionCommentRepository.findAllByGroupNumAndStep(parentQuestionComment.getId(), parentStep + 1);
                }
                int del = 1;
                for (QuestionComment questionComment : questionComments) {
                    List<QuestionComment> childComments = questionCommentRepository.findAllByGroupNum(questionComment.getId());
                    if (!childComments.isEmpty()) {
                        oneStepUp = questionComments;
                        questionComments = childComments;
                        del = 1;
                        break;
                    }
                    questionCommentRepository.delete(questionComment);
                    del = 0;
                }
                if (del == 0) {
                    questionComments.clear();
                }
            }
            questionCommentRepository.delete(parentQuestionComment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("QuestionCommentService.deleteComment() : 에러 발생");
        }
    }

    //  내가 작성한 게시판 댓글 리스트 가져오기
    public List<QuestionCommentDTO.QnACommentListDTO> getMyCommentList(int memberId, Pageable pageable) {
        try {
            // 엔티티 리스트에 담기. 현재 댓글의 답글 step으로 찾아야 하므로 step + 1
            Page<Object[]> pages = questionCommentRepository.getMyList(memberId, pageable);
            List<Object[]> objects = pages.getContent();
            List<QuestionCommentDTO.QnACommentListDTO> myListResponseDTOS = new ArrayList<>();
            // 반복문으로 DTO 리스트에 넣기
            for (Object[] object : objects) {
                // DTO에 담기
                QuestionCommentDTO.QnACommentListDTO myListDTO = new QuestionCommentDTO.QnACommentListDTO(object);
                myListResponseDTOS.add(myListDTO);
            }
            return myListResponseDTOS;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("BoardCommentService.getMyCommentList() : 에러 발생");
        }
    }



    /*
    //강사가 자신이 답변한 목록들 볼 수 있게 하는 부분
    public List<QuestionCommentDTO.QnAAnswerListDTO> getQnAAnswerList(int memberId, LocalDate start, LocalDate end, int page, int size) {
        // 페이징을 위한 처리
        int defaultPage = page - 1;
        if (defaultPage < 0){
            throw new IllegalArgumentException("페이지오류");
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();


        Pageable pageable = PageRequest.of(defaultPage, size, Sort.by(Sort.Direction.DESC, "createdDay"));

        Member member = memberRepository.findByMemberId(memberId);
        List<QuestionCommentDTO.QnAAnswerListDTO> qnaAnswerListDTOs = new ArrayList<>();


        if (member != null && member.getRole() == 1) {
            Page<Answer> answersPage = answerRepository.findByMemberAndCreatedDayBetween(member, startDateTime, endDateTime, pageable);

            for (Answer answer : answersPage) {
                Question question = answer.getQuestion();

                QuestionCommentDTO.QnAAnswerListDTO qnaAnswerListDTO = QuestionCommentDTO.QnAAnswerListDTO.builder()
                        .qnaId(answer.getId())
                        .title(question.getTitle())
                        .questionId(question.getId())
                        .memberId(question.getMember().getId())
                        .nickname(question.getMember().getNickname())
                        .date(answer.getCreatedDay())
                        .lectureId(question.getLecture().getId())
                        .lectureName(question.getLecture().getName())
                        .answerId(answer.getId())
                        .build();

                qnaAnswerListDTOs.add(qnaAnswerListDTO);
            }
        }

        return qnaAnswerListDTOs;
    }
*/
}
