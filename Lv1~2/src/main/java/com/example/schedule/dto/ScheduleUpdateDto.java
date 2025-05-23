package com.example.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleUpdateDto {
    private String title;
    private String task;
    private String author;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password; // 비밀번호 검증을 위해 포함
}
