package com.example.schedule.controller;

import com.example.schedule.dto.*;
import com.example.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Schedule Controller
 */
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 일정 생성 API
     * @param requestDto 일정 생성 요청 객체
     * @return 생성된 일정 응답 DTO
     */
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(@RequestBody ScheduleRequestDto requestDto) {
        return new ResponseEntity<>(scheduleService.createSchedule(requestDto), HttpStatus.CREATED);
    }

    /**
     * 일정 전체 조회 API (작성자, 수정일 필터 가능)
     * @param author 작성자 (선택)
     * @param modifiedDate 수정일 (선택)
     * @return 일정 리스트
     */
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate modifiedDate
    ) {
        //System.out.println("author : controller = " + author);
        return ResponseEntity.ok(scheduleService.getAllSchedules(author, modifiedDate));
    }


    /**
     * 일정 단건 조회 API
     * @param id 식별자
     * @return 해당 일정 응답 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(@PathVariable Long id) {
        return new ResponseEntity<>(scheduleService.getScheduleById(id), HttpStatus.OK);
    }

    /**
     * 일정 수정 API
     * @param id 식별자
     * @param updateDto 수정 요청 객체 (비밀번호 포함)
     * @return 수정된 일정 응답 DTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleUpdateDto updateDto
    ) {
        return new ResponseEntity<>(scheduleService.updateSchedule(id, updateDto), HttpStatus.OK);
    }

    /**
     * 일정 삭제 API
     * @param id 식별자
     * @param passwordDto 비밀번호 (Query Parameter)
     * @return 200 OK
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(
            @PathVariable Long id,
            @RequestBody PasswordDto passwordDto
    ) {
        scheduleService.deleteSchedule(id, passwordDto.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}



