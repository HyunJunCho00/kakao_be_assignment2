package com.example.bigschedule.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ScheduleRequestDto {

    @Size(max=50,message="제목은 50자 이하만 가능합니다.")
    private String title;

    @NotBlank(message = "할일(task)은 필수입니다.")
    @Size(max = 200, message = "할일은 200자 이하만 가능합니다.")
    private String task;

    private Long userId;

    private AuthDto auth;
}

