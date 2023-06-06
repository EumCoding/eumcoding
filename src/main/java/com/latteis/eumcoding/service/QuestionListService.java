package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.QuestionDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Question;
import com.latteis.eumcoding.persistence.AnswerRepository;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionListService {

    private final QuestionRepository questionRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;

    //이상하게 이건 Pageable pageable을 사용하면 page정보들이 다 넘어와서
    //page,size를 사용하기로함
    public List<QuestionDTO.MyQuestionListDTO> getMyQuestions(int memberId, LocalDate start, LocalDate end, int page, int size) {

        //페이지를 1부터 시작하게함
        int DefaultPage = page - 1;
        if (DefaultPage < 0){
            throw new IllegalArgumentException("페이지오류");
        }

        // LocalDate를 LocalDateTime으로 변환. start는 그 날의 시작 시간, end는 다음 날의 시작 시간(즉, end 날짜의 23:59:59)으로 설정한다.
        // 내가 쓴 질문 글 불러올때 start,end는 LocalDate타입. 하지만 DB에는 createdDay가 LocalDateTime형식이라
        // 둘의 타입이 다르기때문에 맞춰주는 용도
        // 안그러면 repository에 LocalDateTime으로 해놔야하는데 지금 이 메서드에서도.
        // 이러면 검색할때 start = 2023-05-1223:33:33 이런식으로 검색해야함
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();

        // 해당 멤버가 작성한 질문을 지정된 기간 내에 찾기, 결과는 페이지네이션
        Pageable pageable = PageRequest.of(DefaultPage, size, Sort.by(Sort.Direction.DESC, "title"));


        Page<Question> questionsPage = questionRepository.findAllByMemberAndCreatedDayBetween(memberId, startDateTime, endDateTime, pageable);


        List<QuestionDTO.MyQuestionListDTO> myQuestionListDTOS = questionsPage.getContent().stream().map(question -> {
            // 해당 질문에 대한 답변 유무 확인
            int answerStatus = answerRepository.existsByQuestion(question.getId()) ? 1 : 0;

            return QuestionDTO.MyQuestionListDTO.builder()
                    .nickname(question.getMember().getNickname())
                    .qnaId(question.getId())
                    .title(question.getTitle())
                    .answer(answerStatus)
                    .date(question.getCreatedDay())
                    .lectureId(question.getLecture().getId())
                    .lectureName(question.getLecture().getName())
                    .build();
        }).collect(Collectors.toList());

        return myQuestionListDTOS;
    }



    //해당 과목에 대한 질문들 가져오기
    public List<QuestionDTO.QnAQuestionListDTO> getQuestionList(int lectureId, int page) {
        //페이지를 1부터 시작하게함
        int DefaultPage = page - 1;
        if (DefaultPage < 0){
            throw new IllegalArgumentException("페이지오류");
        }
        int size = 10;
        Pageable pageable = PageRequest.of(DefaultPage, size, Sort.Direction.DESC, "createdDay");
        Page<Question> questionPage = questionRepository.findByLectureId(lectureId,pageable);

        List<QuestionDTO.QnAQuestionListDTO> questionList = questionPage.getContent().stream().map(question ->{
                    // 해당 질문에 대한 답변 유무 확인
                int answerStatus = answerRepository.existsByQuestion(question.getId()) ? 1 : 0;

                return QuestionDTO.QnAQuestionListDTO.builder()
                        .qnaId(question.getId())
                        .memberId(question.getMember().getId())
                        .lectureId(question.getLecture().getId())
                        .answer(answerStatus)
                        .title(question.getTitle())
                        .date(question.getCreatedDay())
                        .lectureName(question.getLecture().getName())
                        .build();
                }).collect(Collectors.toList());

        return questionList;
    }
}