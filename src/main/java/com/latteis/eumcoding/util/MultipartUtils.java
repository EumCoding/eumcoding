package com.latteis.eumcoding.util;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Slf4j
@UtilityClass
public class MultipartUtils {

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png"
    );

    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
            "video/mp4",
            "video/avi",
            "video/mkv"
    );

    public static boolean isImage(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType);
    }

    public static boolean isVideo(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        return contentType != null && ALLOWED_VIDEO_TYPES.contains(contentType);
    }

    public static File saveImage(MultipartFile multipartFile, File directory, String fileName) throws Exception {
        return saveFile(multipartFile, directory, fileName, ALLOWED_IMAGE_TYPES, "이미지 파일");
    }

    public static File saveVideo(MultipartFile multipartFile, File directory, String fileName) throws Exception {
        return saveFile(multipartFile, directory, fileName, ALLOWED_VIDEO_TYPES, "비디오 파일");
    }

    private static File saveFile(MultipartFile multipartFile, File directory, String fileName,
                                 List<String> allowedTypes, String fileTypeDescription) throws Exception {
        log.info(multipartFile.getOriginalFilename() + " -> " + fileName + " 저장 시작");

        String contentType = multipartFile.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException(fileTypeDescription + "이 아닙니다. (파일명: " + multipartFile.getOriginalFilename() + ")");
        }

        String fileExtension = contentType.substring(contentType.lastIndexOf("/") + 1);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new Exception("디렉토리 생성 실패: " + directory.getAbsolutePath());
        }

        File newFile = new File(directory, fileName + "." + fileExtension);

        // 동일한 이름의 파일이 존재하는 경우 이름을 변경
        int count = 1;
        while (newFile.exists()) {
            newFile = new File(directory, fileName + "_" + count + "." + fileExtension);
            count++;
        }

        multipartFile.transferTo(newFile);

        log.info(multipartFile.getOriginalFilename() + " -> " + fileName + " 저장 완료 (" + newFile.getAbsolutePath() + ")");

        return newFile;
    }
}