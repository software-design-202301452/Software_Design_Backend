package inu.lecture.software_design.domain.parent.service;

import inu.lecture.software_design.domain.parent.dto.response.MyStudentResponse;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;

    /**
     * 학부모 본인과 연동된 자녀(학생) 정보 조회
     */
    @Transactional(readOnly = true)
    public MyStudentResponse getMyStudent(String username) {
        Parent parent = parentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));

        Student student = parent.getStudent();

        return MyStudentResponse.builder()
                .studentId(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .studentNumber(student.getStudentNumber())
                .phone(student.getPhone())
                .address(student.getAddress())
                .build();
    }
}
