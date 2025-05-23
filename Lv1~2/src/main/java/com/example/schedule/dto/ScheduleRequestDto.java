package com.example.schedule.dto;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class ScheduleRequestDto {
    private String title;
    private String task;
    private String author;
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}


