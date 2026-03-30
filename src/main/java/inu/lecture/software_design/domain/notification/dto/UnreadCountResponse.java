package inu.lecture.software_design.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnreadCountResponse {
    private long count;
}
