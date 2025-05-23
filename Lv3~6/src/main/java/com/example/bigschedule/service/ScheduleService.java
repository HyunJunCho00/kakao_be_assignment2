package com.example.bigschedule.service;

import com.example.bigschedule.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    ScheduleResponseDto saveSchedule(ScheduleRequestDto requestDto);
    List<ScheduleResponseDto> getAllSchedules(
            Long userId, LocalDate modifiedDate, String name, String email, String title, String task
    );
    Page<ScheduleResponseDto> getPagedSchedules(Pageable pageable);

    ScheduleResponseDto getScheduleById(Long id);
    ScheduleResponseDto updateSchedule(Long id, ScheduleUpdateRequestDto request);
    void deleteSchedule(Long id, AuthDto authDto);
}