
package com.latteis.eumcoding.service;


import com.latteis.eumcoding.model.EmailNumber;
import com.latteis.eumcoding.model.EmailToken;
import com.latteis.eumcoding.persistence.EmailNumberRepository;
import com.latteis.eumcoding.persistence.EmailTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailNumberService {
    private final EmailSenderService emailSenderService;
    private final EmailNumberRepository emailNumberRepository;
    @Value("${spring.mail.username}")
    private String from; //프로퍼티에서 mail.username 가져옴

    // 인증 번호로 이메일 전송
    @Transactional
    public int sendVerificationNumber(int id, String childEmail) {
        try {
            Assert.notNull(id, "memberId는 필수입니다");
            Assert.hasText(childEmail, "receiverEmail은 필수입니다.");

            // 6자리의 인증 번호 생성
            int verificationNumber = generateVerificationNumber();
            log.info("Generated verification number: " + verificationNumber);

            // 이메일 전송
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(childEmail);
            mailMessage.setSubject("자녀 연동 인증 번호");
            mailMessage.setText("인증 번호: " + verificationNumber);
            mailMessage.setFrom(from + "@naver.com");
            emailSenderService.sendEmail(mailMessage);

            Optional<EmailNumber> existingEmailNumber = emailNumberRepository.findByMemberId(id);
            EmailNumber emailNumber;
            //이메일 전송 후 인증 번호 저장 or 업데이트
            if(existingEmailNumber.isPresent()){
                emailNumber = existingEmailNumber.get();
                emailNumber.setEmailNumberId(verificationNumber);
                emailNumber.resetExpiration(); // 만료시간을 재설정하는 메서드
                emailNumber.setExpired(0); //0:유효 1:만료
            }else{
                // 존재하지 않는 경우 생성
                emailNumber = EmailNumber.createEmailNumber(id);
                emailNumber.setEmailNumberId(verificationNumber);
            }
            emailNumberRepository.save(emailNumber);

            return verificationNumber; 
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new RuntimeException("인증 번호 전송 중 오류 발생");
        }
    }

    // 6자리의 무작위 인증 번호 생성
    private int generateVerificationNumber() {
        Random random = new Random();
        return random.nextInt(1000000);
    }

    // 매 분마다 실행되는 스케줄러
    // expiration_date에 값이 넘어서면 DB에서 자동으로 expired 가 0->1 (사용가능->사용불가능)으로 업데이트
    @Scheduled(fixedRate = 60000)  // 60,000ms = 1분
    public void checkAndExpireEmailNumbers() {
        List<EmailNumber> emailNumbers = emailNumberRepository.findAllByExpiredFalseAndExpirationDateBefore(LocalDateTime.now());

        for(EmailNumber emailNumber : emailNumbers) {
            emailNumber.setExpired(1);
            emailNumberRepository.save(emailNumber);
        }
    }


}

