package com.example.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleUpdateDto {
    private String title;
    private String task;
    private String author;
    private String password; // 비밀번호 검증을 위해 포함
}
