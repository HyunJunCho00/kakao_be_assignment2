package com.example.schedule.service;

import com.example.schedule.dto.ScheduleRequestDto;
import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.dto.ScheduleUpdateDto;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto);
    List<ScheduleResponseDto> getAllSchedules(String author, LocalDate modifiedDate);
    ScheduleResponseDto getScheduleById(Long id);
    ScheduleResponseDto updateSchedule(Long id, ScheduleUpdateDto updateDto);
    void deleteSchedule(Long id, String password);
}
