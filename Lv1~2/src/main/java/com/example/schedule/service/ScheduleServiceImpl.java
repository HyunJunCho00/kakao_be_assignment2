package com.example.schedule.service;

import com.example.schedule.dto.*;
import com.example.schedule.entity.Schedule;
import com.example.schedule.repository.ScheduleJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleJdbcRepository scheduleRepository;

    @Override
    public ScheduleResponseDto createSchedule(ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule();
        schedule.setTitle(requestDto.getTitle());
        schedule.setTask(requestDto.getTask());
        schedule.setAuthor(requestDto.getAuthor());
        schedule.setPassword(requestDto.getPassword());
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());

        return scheduleRepository.save(schedule);
    }

    @Override
    public List<ScheduleResponseDto> getAllSchedules(String author, LocalDate modifiedDate) {
        // author가 빈 문자열이면 null로 치환해도 좋음
        if (author != null && author.isEmpty()) {
            author = null;
        }
        return scheduleRepository.findAllByModifiedDateAndWriter(modifiedDate, author);
    }

    @Override
    public ScheduleResponseDto getScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 일정이 존재하지 않습니다."));
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getTask(),
                schedule.getAuthor(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Override
    public ScheduleResponseDto updateSchedule(Long id, ScheduleUpdateDto updateDto) {
        // 비밀번호 체크는 repository.update() 내부에서 함 (예외 던짐)
        int updatedCount = scheduleRepository.update(
                id,
                updateDto.getTask(),
                updateDto.getAuthor(),
                updateDto.getPassword());

        if (updatedCount == 0) {
            throw new IllegalArgumentException("업데이트 실패");
        }

        // 업데이트 후 다시 조회해서 DTO 반환
        return getScheduleById(id);
    }

    @Override
    public void deleteSchedule(Long id, String password) {
        int deletedCount = scheduleRepository.delete(id, password);
        if (deletedCount == 0) {
            throw new IllegalArgumentException("삭제 실패");
        }
    }
}
