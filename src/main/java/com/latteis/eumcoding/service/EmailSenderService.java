
package com.latteis.eumcoding.service;

import com.latteis.eumcoding.model.Lecture;
import com.latteis.eumcoding.model.Member;
import com.latteis.eumcoding.persistence.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

@Service
@RequiredArgsConstructor
// 이메일을 전송하는 서비스
public class EmailSenderService {
    private final JavaMailSender javaMailSender;

    @Async //비동기
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }
}

