package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.QuestionDTO;
import com.latteis.eumcoding.dto.ReviewDTO;
import com.latteis.eumcoding.dto.TeacherListQuestionDTO;
import com.latteis.eumcoding.dto.TeacherListReviewDTO;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.Question;
import com.latteis.eumcoding.model.Review;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.QuestionCommentRepository;
import com.latteis.eumcoding.persistence.QuestionRepository;
import com.latteis.eumcoding.persistence.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherQuestionAndReviewListService {

    private final QuestionRepository questionRepository;
    private final QuestionCommentRepository questionCommentRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;

    // 도메인
    @Value("${server.domain}")
    private String domain;
    // 포트번호
    @Value("${server.port}")
    private String port;


    /**
     *선생 계정으로 로그인하면, 자신한테 질문한 학생들
     * 질문 글들의 모음
     * 11.12
     */
    public TeacherListQuestionDTO getMyStudentQuestions(int memberId, LocalDate start, LocalDate end, Integer lectureId, int page, int size) {

        Member member = memberRepository.findByMemberId(memberId);
        if(member.getRole() != 1){
            throw new IllegalArgumentException("선생님 계정으로만 접근이 가능합니다.");
        }
        //페이지를 1부터 시작하게함
        int DefaultPage = page - 1;
        if (DefaultPage < 0){
            throw new IllegalArgumentException("페이지오류");
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();


        // 해당 멤버가 작성한 질문을 지정된 기간 내에 찾기, 결과는 페이지네이션
        Pageable pageable = PageRequest.of(DefaultPage, size, Sort.by(Sort.Direction.DESC, "title"));

        Page<Question> questionsPage = questionRepository.findAllByMemberAndMonthCreatedDayBetween(startDateTime, endDateTime,lectureId,pageable);
        long count = questionRepository.countTeacherQuestions(startDateTime,endDateTime,lectureId);

        List<TeacherListQuestionDTO.StudentQuestionListDTO> studentQuestionListDTOS = new ArrayList<>();
        for(Question questions : questionsPage) {
            // 해당 질문에 대한 답변 유무 확인
            int questionCommentStatus = questionCommentRepository.existsByQuestion(questions.getId()) ? 1 : 0;
            // 강의썸네일
            String lectureThumbnail = domain + port + "/eumCodingImgs/lecture/thumb/" + questions.getLecture().getThumb();

            if(questions.getLecture().getMember().getId() == memberId){
                TeacherListQuestionDTO.StudentQuestionListDTO sq = TeacherListQuestionDTO.StudentQuestionListDTO.builder()
                        .nickname(questions.getMember().getNickname())
                        .qnaId(questions.getId())
                        .title(questions.getTitle())
                        .answer(questionCommentStatus)
                        .date(questions.getCreatedDay())
                        .lectureId(questions.getLecture().getId())
                        .lectureName(questions.getLecture().getName())
                        .lectureThumb(lectureThumbnail)
                        .build();
                studentQuestionListDTOS.add(sq);
            }
        }

        return TeacherListQuestionDTO.builder()
                .count(count)
                .teacherMyReviewList(studentQuestionListDTOS)
                .build();
    }


    /**
     *선생 계정으로 로그인하면, 나에 대한 리뷰 모음
     * 11.12
     */
    public TeacherListReviewDTO getMyLectureReviews(int memberId, LocalDate start, LocalDate end, Integer lectureId, int page, int size) {

        Member member = memberRepository.findByMemberId(memberId);
        if(member.getRole() != 1){
            throw new IllegalArgumentException("선생님 계정으로만 접근이 가능합니다.");
        }
        //페이지를 1부터 시작하게함
        int DefaultPage = page - 1;
        if (DefaultPage < 0){
            throw new IllegalArgumentException("페이지오류");
        }

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();


        Pageable pageable = PageRequest.of(DefaultPage, size, Sort.by(Sort.Direction.DESC, "student.nickname"));

        Page<Object[]> reviewPage = reviewRepository.getTeacherMyReviewListByDate(memberId,startDateTime, endDateTime,lectureId,pageable);
        long count = reviewRepository.countTeacherReviews(memberId,startDateTime,endDateTime,lectureId);
        List<Object[]> studentReviewListDTOS = reviewPage.getContent();
        List<TeacherListReviewDTO.ListTeacherResponseDTO> myListReviewList = new ArrayList<>();
        for(Object[] object : studentReviewListDTOS){
            TeacherListReviewDTO.ListTeacherResponseDTO lt = TeacherListReviewDTO.ListTeacherResponseDTO.builder()
                    .id(Integer.parseInt(String.valueOf(object[0])))
                    .lectureId(Integer.parseInt(String.valueOf(object[1])))
                    .memberId(Integer.parseInt(String.valueOf(object[2])))
                    .nickname(String.valueOf(object[3]))
                    .content(String.valueOf(object[4]))
                    .rating(Integer.parseInt(String.valueOf(object[5])))
                    .createdDay(((Timestamp) object[6]).toLocalDateTime())
                    .heart(Integer.parseInt(String.valueOf(object[7])))
                    .build();
        myListReviewList.add(lt);
        }
        return TeacherListReviewDTO.builder()
                .count(count)
                .teacherMyReviewList(myListReviewList)
                .build();
    }
}