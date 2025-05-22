package com.example.schedule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleRequestDto {
    private String title;
    private String task;
    private String author;
    private String password;
}

