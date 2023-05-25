package com.latteis.eumcoding.util;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@UtilityClass
public class MultipartUtils {

    // 사용 가능한 이미지 형식
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png"
//            "image/gif"
    );

    /**
     * MultipartFile이 이미지 형식인지 확인합니다.
     */
    public static boolean isImage(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        if (contentType == null) {
            return false;
        }

        return ALLOWED_IMAGE_TYPES.contains(contentType);
    }

    /**
     * MultipartFile을 지정된 폴더에 이미지 파일로 저장합니다.
     * 저장한 후 저장한 File을 반환합니다.
     * @param directory 저장할 폴더
     * @param fileName 파일 이름 (확장자 제외)
     * @return 저장된 파일을 반환합니다.
     */
    @SneakyThrows
    public static File saveImage(MultipartFile multipartFile, File directory, String fileName) {
        log.info(multipartFile.getOriginalFilename() + " -> " + fileName + " 저장 시작");

        // 이미지 파일인지 체크
        Preconditions.checkArgument(isImage(multipartFile), "이미지 파일이 아닙니다. (파일명: %s)", multipartFile.getOriginalFilename());

        // 확장자 계산
        String contentType = multipartFile.getContentType();
        String fileExtension = contentType.substring(contentType.lastIndexOf("/") + 1);

        // 새로운 파일 경로 지정
        directory.mkdirs();
        File newFile = new File(directory, fileName + "." + fileExtension);

        // 파일 저장
        multipartFile.transferTo(newFile);

        log.info(multipartFile.getOriginalFilename() + " -> " + fileName + " 저장 완료 (" + newFile.getAbsolutePath() + ")");

        return newFile;
    }

}