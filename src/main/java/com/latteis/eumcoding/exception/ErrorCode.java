package com.latteis.eumcoding.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // 400 BAD_REQUEST 잘못된 요청
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),

    // 401 UNAUTHORIZED 권한 없음
    USER_UNREGISTERED(HttpStatus.UNAUTHORIZED, "등록되지 않은 사용자입니다."),
    TEST_MEMBER_UNREGISTERED(HttpStatus.UNAUTHORIZED, "해당 테스트 멤버는 등록되지 않은 사용자입니다."),
    STUDENT_INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "학생 권한이 없습니다."),
    TEACHER_INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "강사 권한이 없습니다."),
    ADMIN_INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "관리자 권한이 없습니다."),
    PARENTS_INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "학부모 권한이 없습니다."),

    // 404 NOT_FOUND 잘못된 리소스 접근
    LECTURE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 강의 ID 입니다."),
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 섹션 ID 입니다."),
    VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 비디오 ID 입니다."),
    PERIOD_OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 기간 옵션 입니다."),
    VIDEO_TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 비디오 테스트 입니다."),
    MAIN_TEST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메인 테스트 입니다."),

    // 409 CONFLICT 중복된 리소스
    ALREADY_SAVED_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),

    // 412 PRECONDITION_FAILED 사전 조건 실패
    TWO_DATE_PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, "날짜를 모두 선택해주세요."),
    VIDEO_PRECONDITION_FAILED(HttpStatus.PRECONDITION_FAILED, "이전 영상을 시청한 뒤 시도하세요."),

    // 500 INTERNAL_SERVER_ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러입니다.");

    private final HttpStatus status;
    private final String message;

}
