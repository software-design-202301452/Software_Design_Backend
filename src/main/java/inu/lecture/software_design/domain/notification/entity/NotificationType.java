package inu.lecture.software_design.domain.notification.entity;

public enum NotificationType {
    /** 성적 입력/수정 */
    GRADE_UPDATED,
    /** 피드백 작성/공개 */
    FEEDBACK_PUBLISHED,
    /** 상담 내역 등록/수정 */
    COUNSELING_UPDATED,
    /** 학생부 수정 */
    STUDENT_RECORD_UPDATED,
    /** 기타 */
    GENERAL
}
