package com.example.bigschedule.service;

import com.example.bigschedule.dto.*;
import com.example.bigschedule.entity.Schedule;
import com.example.bigschedule.entity.User;
import com.example.bigschedule.exception.CustomException;
import com.example.bigschedule.exception.ErrorCode;
import com.example.bigschedule.repository.ScheduleJdbcRepository;
import com.example.bigschedule.validation.OwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleJdbcRepository scheduleJdbcRepository;
    private final OwnershipValidator ownershipValidator;
    private final UserService userService;

    @Override
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto requestDto) {
        User user = userService.findUserByAuth(requestDto.getAuth());

        Schedule schedule = new Schedule();
        schedule.setTitle(requestDto.getTitle());
        schedule.setTask(requestDto.getTask());
        schedule.setUser(user);

        scheduleJdbcRepository.saveSchedule(schedule);
        return toDto(schedule);
    }

    @Override
    public List<ScheduleResponseDto> getAllSchedules(
            Long userId, LocalDate modifiedDate, String name, String email, String title, String task
    ) {
        List<Schedule> schedules = scheduleJdbcRepository.findSchedules(userId, modifiedDate, name, email, title, task);

        return schedules.stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }
    @Override
    public Page<ScheduleResponseDto> getPagedSchedules(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int offset = (int) pageable.getOffset();

        List<Schedule> schedules = scheduleJdbcRepository.findAllPagedWithUserName(offset, pageSize);
        long total = scheduleJdbcRepository.countAll();

        List<ScheduleResponseDto> content = schedules.stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }


    @Override
    public ScheduleResponseDto getScheduleById(Long id) {
        Schedule schedule = scheduleJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));
        return toDto(schedule);
    }

    @Override
    public ScheduleResponseDto updateSchedule(Long id, ScheduleUpdateRequestDto request) {
        Schedule schedule = scheduleJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        if (request.getAuth() == null) {
            throw new CustomException(ErrorCode.AUTH_INFO_REQUIRED);
        }

        ownershipValidator.validate(
                schedule,
                request.getAuth().getEmail(),
                request.getAuth().getName(),
                request.getAuth().getPassword()
        );

        if (request.getSchedule() == null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST_DATA);
        }

        String newTitle = request.getSchedule().getTitle();
        String newTask = request.getSchedule().getTask();

        Map<String, Object> fieldsToUpdate = new HashMap<>();

        if (newTitle != null && !newTitle.isBlank()) {
            if (newTitle.length() > 50) {
                throw new CustomException(ErrorCode.TITLE_TOO_LONG);
            }
            fieldsToUpdate.put("title", newTitle);
        }

        if (newTask != null && !newTask.isBlank()) {
            if (newTask.length() > 200) {
                throw new CustomException(ErrorCode.TASK_TOO_LONG);
            }
            fieldsToUpdate.put("task", newTask);
        }


        if (fieldsToUpdate.isEmpty()) {
            throw new CustomException(ErrorCode.NO_FIELDS_TO_UPDATE);
        }

        // updated_at은 레포지토리에서 자동으로 추가
        scheduleJdbcRepository.updateScheduleFields(id, fieldsToUpdate);

        // 다시 DB에서 최신 Schedule 객체 조회해서 Response하게 된다..
        Schedule updated = scheduleJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        return toDto(updated);

    }

    @Override
    public void deleteSchedule(Long id, AuthDto authDto) {
        Schedule schedule = scheduleJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_NOT_FOUND));

        ownershipValidator.validate(schedule, authDto.getEmail(), authDto.getName(), authDto.getPassword());

        scheduleJdbcRepository.delete(schedule);
    }

    private ScheduleResponseDto toDto(Schedule schedule) {
        return new ScheduleResponseDto(schedule);
    }
}
