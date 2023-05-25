package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.AnswerDTO;
import com.latteis.eumcoding.model.Answer;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Question;
import com.latteis.eumcoding.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionCommentService {

    private final QuestionRepository questionRepository;
    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;

    public AnswerDTO.AnswerWriteDTO writeComment(int memberId, AnswerDTO.AnswerWriteDTO answerWriteDTO) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        if (member.getRole() != 1) {
            throw new IllegalArgumentException("강사만 답변을 작성할 수 있습니다.");
        }

        // 강좌에 대한 멤버 정보.(해당 멤버가 강좌를 만들었는지.)
        Question question = questionRepository.findById(answerWriteDTO.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다."));
        Lecture lecture = question.getLecture();
        if (lecture.getMember().getId() != memberId) {
            throw new IllegalArgumentException("강사만 해당 강의에 대한 질문에 답변할 수 있습니다.");
        }

        // 질문에 대한 답이 있는지 체크
        boolean completeAnswer = answerRepository.existsByQuestion(question.getId());
        if (completeAnswer) {
            throw new IllegalStateException("이미 답변을 한 질문입니다.");
        }


        Answer answer = Answer.builder()
                .question(question)
                .member(member)
                .content(answerWriteDTO.getContent())
                .createdDay(LocalDateTime.now())
                .build();

        answerRepository.save(answer);

        AnswerDTO.AnswerWriteDTO answerWriteDTOResult = AnswerDTO.AnswerWriteDTO.builder()
                .questionId(answer.getQuestion().getId())
                .content(answer.getContent())
                .build();

        return answerWriteDTOResult;
    }

    public AnswerDTO.AnswerUpdateDTO updateComment(int memberId, int answerId, AnswerDTO.AnswerUpdateDTO answerUpdateDTO) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        if (member.getRole() != 1) {
            throw new IllegalArgumentException("강사만 답변을 수정할 수 있습니다.");
        }


        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        // 자기가 작성한 답변인지 체크
        if (answer.getMember().getId() != memberId) {
            throw new IllegalArgumentException("강사는 자신이 작성한 답변만 수정할 수 있습니다.");
        }

        // 수정된 답변+날짜
        answer.setContent(answerUpdateDTO.getContent());
        answer.setUpdatedDay(LocalDateTime.now());


        answerRepository.save(answer);


        AnswerDTO.AnswerUpdateDTO answerUpdateDTOResult = AnswerDTO.AnswerUpdateDTO.builder()
                .answerId(answer.getId())
                .content(answer.getContent())
                .build();

        return answerUpdateDTOResult;
    }


    public AnswerDTO.AnswerDeleteDTO deleteComment(int memberId, int answerId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        if (member.getRole() != 1) {
            throw new IllegalArgumentException("강사만 답변을 삭제할 수 있습니다.");
        }

        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));

        // 자기가 작성한 답변인지 체크
        if (answer.getMember().getId() != memberId) {
            throw new IllegalArgumentException("강사는 자신이 작성한 답변만 삭제할 수 있습니다.");
        }

        answerRepository.delete(answer);

        AnswerDTO.AnswerDeleteDTO answerDeleteDTOResult = AnswerDTO.AnswerDeleteDTO.builder()
                .answerId(answerId)
                .build();

        return answerDeleteDTOResult;
    }



}
