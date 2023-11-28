package com.latteis.eumcoding.service;

import com.latteis.eumcoding.exception.ErrorCode;
import com.latteis.eumcoding.exception.ResponseMessageException;
import com.latteis.eumcoding.model.MainTestAnswer;
import com.latteis.eumcoding.util.blockCoding.Block;
import com.latteis.eumcoding.util.blockCoding.BlockCodeToJavaConverter;
import com.latteis.eumcoding.util.blockCoding.JavaCodeExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainTestBlockService {

    private final BlockCodeToJavaConverter blockCodeToJavaConverter;

    private final JavaCodeExecutor javaCodeExecutor = new JavaCodeExecutor();


    /**
     * @param authentication 로그인 정보
     * @param blockList 블록 리스트
     * @return string
     */
    public String convertBlocks(Authentication authentication, List<Block> blockList) {

        if (blockList.isEmpty()) {
            throw new ResponseMessageException(ErrorCode.INVALID_PARAMETER);
        }

        // 학생 답안을 자바 코드로 변환
        String testString = blockCodeToJavaConverter.convertToJavaCode(blockList);

        // 변환된 답안을 컴파일
        String testAnswerString = null;

        try{
            testAnswerString = javaCodeExecutor.executeJavaCode(testString);
        }catch(Exception e){
            e.printStackTrace();
            log.info("컴파일 에러");
        }

        // 문자열의 공백을 제거하고 비교
        String formattedTestAnswer = testAnswerString.toString().trim();

        return formattedTestAnswer;

    }
}
