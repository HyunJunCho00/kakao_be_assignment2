package com.example.bigschedule.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 해당 이메일은 존재합니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    NAME_MISMATCH(HttpStatus.BAD_REQUEST, "이름이 일치하지 않습니다."),
    EMAIL_MISMATCH(HttpStatus.BAD_REQUEST, "이메일이 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_SCHEDULE(HttpStatus.CONFLICT, "이미 해당 task에 대한 스케줄이 존재합니다."),
    AUTH_INFO_REQUIRED(HttpStatus.BAD_REQUEST, "인증 정보가 누락되었습니다."),
    INVALID_REQUEST_DATA(HttpStatus.BAD_REQUEST, "잘못된 요청 데이터입니다."),
    NO_FIELDS_TO_UPDATE(HttpStatus.BAD_REQUEST, "수정할 항목이 없습니다."),
    TITLE_TOO_LONG(HttpStatus.BAD_REQUEST, "title은 50자 이하로 입력해야 합니다."),
    TASK_TOO_LONG(HttpStatus.BAD_REQUEST, "task는 200자 이하로 입력해야 합니다."),


    // 필요한 에러 계속 추가하면 될듯..
    ;


    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() { return status; }
    public String getMessage() { return message; }
}

