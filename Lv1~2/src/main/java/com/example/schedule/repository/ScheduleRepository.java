package com.example.schedule.repository;

import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.entity.Schedule;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    // 일정 저장
    ScheduleResponseDto save(Schedule schedule);

    // 전체 조회 (수정일, 작성자명 필터 조건 가능)
    List<ScheduleResponseDto> findAllByModifiedDateAndWriter(LocalDate modifiedDate, String writer);

    // ID로 단건 조회
    Optional<Schedule> findById(Long id);

    // 일정 수정 (할일, 작성자명 수정, 비밀번호 확인)
    int update(Long id, String todo, String writer, String password);

    // 일정 삭제 (비밀번호 확인)
    int delete(Long id, String password);

}