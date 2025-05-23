package com.example.bigschedule.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ScheduleUpdateRequestDto {
    @NotNull(message = "일정 정보는 필수입니다.")
    private ScheduleUpdateDto schedule;

    @NotNull(message = "인증 정보는 필수입니다.")
    private AuthDto auth;
}

