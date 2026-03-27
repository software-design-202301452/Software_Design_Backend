package inu.lecture.software_design.domain.subject.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectResponse {

    private Long id;
    private String name;
    private String description;
}
