package inu.lecture.software_design.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 입력값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_002", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_003", "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_004", "요청한 리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_005", "서버 내부 오류가 발생했습니다."),

    // JWT
    JWT_ENTRY_POINT(HttpStatus.UNAUTHORIZED, "JWT_001", "인증이 필요합니다. 로그인 후 이용해주세요."),
    JWT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "JWT_002", "접근 권한이 없습니다."),
    JWT_INVALID(HttpStatus.UNAUTHORIZED, "JWT_003", "유효하지 않은 토큰입니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "JWT_004", "만료된 토큰입니다."),

    // 인증/사용자
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "USER_002", "이미 사용 중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_003", "이미 사용 중인 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "USER_004", "비밀번호가 일치하지 않습니다."),
    INVALID_TEACHER_CODE(HttpStatus.BAD_REQUEST, "USER_005", "유효하지 않은 교사 인증 코드입니다."),
    INVALID_LINK_CODE(HttpStatus.BAD_REQUEST, "USER_006", "유효하지 않은 학생 연동 코드입니다."),
    STUDENT_ALREADY_REGISTERED(HttpStatus.CONFLICT, "USER_007", "해당 학번으로 이미 가입된 계정이 존재합니다."),

    // 교사
    TEACHER_NOT_FOUND(HttpStatus.NOT_FOUND, "TEACHER_001", "교사를 찾을 수 없습니다."),

    // 학생
    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDENT_001", "학생을 찾을 수 없습니다."),

    // 학부모
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PARENT_001", "학부모를 찾을 수 없습니다."),

    // 과목
    SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBJECT_001", "과목을 찾을 수 없습니다."),
    SUBJECT_ALREADY_EXISTS(HttpStatus.CONFLICT, "SUBJECT_002", "이미 존재하는 과목명입니다."),

    // 성적
    GRADE_NOT_FOUND(HttpStatus.NOT_FOUND, "GRADE_001", "성적 정보를 찾을 수 없습니다."),
    GRADE_ALREADY_EXISTS(HttpStatus.CONFLICT, "GRADE_002", "이미 등록된 성적입니다."),

    // 상담
    COUNSELING_NOT_FOUND(HttpStatus.NOT_FOUND, "COUNSELING_001", "상담 내역을 찾을 수 없습니다."),

    // 피드백
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK_001", "피드백을 찾을 수 없습니다."),

    // 학생부
    STUDENT_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "RECORD_001", "학생부 정보를 찾을 수 없습니다."),
    STUDENT_RECORD_ALREADY_EXISTS(HttpStatus.CONFLICT, "RECORD_002", "해당 학생의 동일 학년도/학기 학생부가 이미 존재합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
