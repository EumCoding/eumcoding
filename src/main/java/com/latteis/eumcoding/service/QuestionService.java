package com.latteis.eumcoding.service;

import com.latteis.eumcoding.dto.QuestionDTO;
import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.model.PayLecture;
import com.latteis.eumcoding.model.Question;
import com.latteis.eumcoding.persistence.LectureRepository;
import com.latteis.eumcoding.persistence.MemberRepository;
import com.latteis.eumcoding.persistence.PayLectureRepository;
import com.latteis.eumcoding.persistence.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    private final LectureRepository lectureRepository;

    private final MemberRepository memberRepository;


    @Value("${file.path}")
    private String filePath;

    @Transactional
    public QuestionDTO.writeQuestionDTO writeQuestion(int memberId, QuestionDTO.writeQuestionDTO writeQuestionDTO) throws IOException {
        Member member = memberRepository.findByMemberId(memberId);
        if (member.getRole() != 0) {
            throw new IllegalArgumentException("회원만 질문을 작성할 수 있습니다.");
        }

        Lecture lecture = lectureRepository.findById(writeQuestionDTO.getLectureId());
        if (lecture == null) {
            throw new IllegalArgumentException("해당 강좌가 없습니다.");
        }

        log.info("파일경로 확인" + filePath);

        Question question = Question.builder()
                .member(member)
                .lecture(lecture)
                .title(writeQuestionDTO.getTitle())
                .content(writeQuestionDTO.getContent())
                .image(null)
                .createdDay(LocalDateTime.now())
                .updatedDay(LocalDateTime.now())
                .build();


        question = questionRepository.save(question);

        // 이미지 파일이 있을 경우 저장
        if (writeQuestionDTO.checkProfileImgRequestNull() && !writeQuestionDTO.getImgRequest().isEmpty()) {

            MultipartFile multipartFile = writeQuestionDTO.getImgRequest().get(0);

            if (!multipartFile.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

                //String absolutePath = "C:" +File.separator + "eumCoding" + File.separator + "member";
                String absolutePath = filePath;
                File file = new File(absolutePath);

                if (!file.exists()) {
                    boolean wasSuccessful = file.mkdirs();
                    if (!wasSuccessful) {
                        log.warn("file : was not successful");
                    }
                }
                while (true) {
                    String originalFileExtension;
                    String contentType = multipartFile.getContentType();

                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else {
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        } else {
                            log.warn("UnauthQuestionService.add() : 지원하지 않는 이미지 형식입니다.");
                            break;
                        }
                    }

                    String new_file_name = String.valueOf(question.getId());
                    question.setImage(new_file_name + originalFileExtension);

                    //file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                    file = new File(absolutePath + (absolutePath.endsWith(File.separator) ? "" : File.separator) + new_file_name + originalFileExtension);

                    multipartFile.transferTo(file);

                    file.setWritable(true);
                    file.setReadable(true);
                    break;
                }
            }

            question = questionRepository.save(question);
        }

        QuestionDTO.writeQuestionDTO writtenQuestion = QuestionDTO.writeQuestionDTO.builder()
                .lectureId(question.getLecture().getId())
                .title(question.getTitle())
                .content(question.getContent())
                .image(question.getImage())
                .build();

        return writtenQuestion;
    }

    @Transactional
    public QuestionDTO.updateQuestionDTO updateQuestion(int memberId, QuestionDTO.updateQuestionDTO updateQuestionDTO) throws IOException {

        int questionId = updateQuestionDTO.getQuestionId();
        Member member = memberRepository.findByMemberId(memberId);
        Question question = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다."));

        // 해당 글을 작성한 작성자인지 확인
        if (question.getMember().getId() != member.getId()) {
            throw new IllegalArgumentException("해당 질문을 작성한 사용자만 질문을 수정할 수 있습니다.");
        }

        if (question.getTitle() != null) {
            question.setTitle(updateQuestionDTO.getTitle());
        }

        if (question.getContent() != null) {
            question.setContent(updateQuestionDTO.getContent());
        }

        question.setUpdatedDay(LocalDateTime.now());


        // 이미지 파일이 있을 경우 저장
        if (updateQuestionDTO.checkProfileImgRequestNull() && !updateQuestionDTO.getImgRequest().isEmpty()) {

            MultipartFile multipartFile = updateQuestionDTO.getImgRequest().get(0);

            if (!multipartFile.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");


                //String absolutePath = "C:" +File.separator + "eumCoding" + File.separator + "member";
                String absolutePath = filePath;
                File file = new File(absolutePath);

                if (!file.exists()) {
                    boolean wasSuccessful = file.mkdirs();
                    if (!wasSuccessful) {
                        log.warn("file : was not successful");
                    }
                }
                while (true) {
                    String originalFileExtension;
                    String contentType = multipartFile.getContentType();

                    if (ObjectUtils.isEmpty(contentType)) {
                        break;
                    } else {
                        if (contentType.contains("image/jpeg")) {
                            originalFileExtension = ".jpg";
                        } else if (contentType.contains("image/png")) {
                            originalFileExtension = ".png";
                        } else {
                            log.warn("UnauthQuestionService.add() : 지원하지 않는 이미지 형식입니다.");
                            break;
                        }
                    }

                    String new_file_name = String.valueOf(question.getId());
                    question.setImage(new_file_name + originalFileExtension);

                    //file = new File(absolutePath + File.separator + new_file_name + originalFileExtension);
                    file = new File(absolutePath + (absolutePath.endsWith(File.separator) ? "" : File.separator) + new_file_name + originalFileExtension);

                    multipartFile.transferTo(file);

                    file.setWritable(true);
                    file.setReadable(true);
                    break;
                }
            }
        }
        question = questionRepository.save(question);

        QuestionDTO.updateQuestionDTO updatedQuestion = QuestionDTO.updateQuestionDTO.builder()
                .questionId(updateQuestionDTO.getQuestionId())
                .title(updateQuestionDTO.getTitle())
                .content(updateQuestionDTO.getContent())
                .image(updateQuestionDTO.getImage())
                .build();

        return updatedQuestion;

    }

    @Transactional
    public void deleteQuestion(int memberId, @RequestBody QuestionDTO.deleteQuestionDTO deleteQuestionDTO){
        int questionId = deleteQuestionDTO.getQuestionId();
        Member member = memberRepository.findByMemberId(memberId);
        Question question = questionRepository.findById(questionId).orElseThrow(()-> new IllegalArgumentException("해당 질문이 없습니다."));

        //글 작성자인지 확인
        if(question.getMember().getId() != member.getId()){
            throw new IllegalArgumentException("해당 질문을 작성한 사용자만 질문을 삭제할 수 있습니다.");
        }
        questionRepository.deleteById(questionId);
    }



}