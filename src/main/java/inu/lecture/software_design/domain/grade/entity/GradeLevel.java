package inu.lecture.software_design.domain.grade.entity;

/**
 * 성취도 등급
 * A(수): 90~100, B(우): 80~89, C(미): 70~79, D(양): 60~69, E(가): 0~59
 */
public enum GradeLevel {
    A, B, C, D, E;

    public static GradeLevel from(double score) {
        if (score >= 90) return A;
        if (score >= 80) return B;
        if (score >= 70) return C;
        if (score >= 60) return D;
        return E;
    }
}
