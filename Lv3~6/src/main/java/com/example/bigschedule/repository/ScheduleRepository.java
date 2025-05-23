package com.example.bigschedule.repository;

import com.example.bigschedule.entity.Schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScheduleRepository {

    Schedule saveSchedule(Schedule schedule);

    void delete(Schedule schedule);

    List<Schedule> findSchedules(
            Long userId, LocalDate modifiedDate, String name, String email, String title, String task
    );

    Optional<Schedule> findById(Long id);

    void updateScheduleFields(Long id, Map<String, Object> fieldsToUpdate);

    // 페이지네이션은 offset, limit 사용

    List<Schedule> findAllPagedWithUserName(int offset, int limit);
    long countAll();

}
