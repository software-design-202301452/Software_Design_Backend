package inu.lecture.software_design.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponseEntity {
    private final String code;
    private final String name;
    private final String message;
}
