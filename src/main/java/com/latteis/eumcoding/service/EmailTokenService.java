/*
package com.latteis.eumcoding.service;


import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailTokenService {
    private final EmailSenderService emailSenderService;
    private final EmailTokenRepository emailTokenRepository;
    @Value("${spring.mail.username}")
    private String from; //프로퍼티에서 mail.username 가져옴

    // 이메일 인증 토큰 생성
    public String createEmailToken(int memberId, String receiverEmail) throws Exception{

        try{
            Assert.notNull(memberId, "memberId는 필수입니다");
            Assert.hasText(receiverEmail, "receiverEmail은 필수입니다.");

            // 이메일 토큰 저장
            EmailTokenEntity emailToken = EmailTokenEntity.createEmailToken(memberId);
            emailTokenRepository.save(emailToken);

            // 이메일 전송
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(receiverEmail);
            mailMessage.setSubject("회원가입 이메일 인증");
            mailMessage.setText("http://localhost:3000/confirm/"+emailToken.getEmailtokenId());
            mailMessage.setFrom(from + "@naver.com"); // 이거 해줘야 오류안남
            emailSenderService.sendEmail(mailMessage);

            return emailToken.getEmailtokenId();    // 인증메일 전송 시 토큰 반환
        }catch (Exception e){
            log.warn(e.getMessage());
            throw new RuntimeException("또 메일에러");
        }

    }

    // 유효한 토큰 가져오기
    public EmailTokenEntity findByIdAndExpirationDateAfterAndExpired(String emailTokenId) throws Exception {
        Optional<EmailTokenEntity> emailToken = emailTokenRepository
                .findByEmailtokenIdAndExpirationdateAfterAndExpired(emailTokenId, LocalDateTime.now(), false);

        // 토큰이 없다면 예외 발생
        return emailToken.orElseThrow(() -> new Exception("no token"));
    }
}
*/
