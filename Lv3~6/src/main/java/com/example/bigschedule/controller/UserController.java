package com.example.bigschedule.controller;

import com.example.bigschedule.dto.ScheduleResponseDto;
import com.example.bigschedule.dto.UserRequestDto;
import com.example.bigschedule.dto.UserResponseDto;
import com.example.bigschedule.dto.UserUpdateRequestDto;
import com.example.bigschedule.service.ScheduleService;
import com.example.bigschedule.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ScheduleService scheduleService;

    // 사용자 생성
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto requestDto) {
        UserResponseDto responseDto = userService.createUser(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // ID로 사용자 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 전체 사용자 목록
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // 해당 사용자 ID의 전체 일정 조회
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleResponseDto>> getUserSchedules(
            @PathVariable Long id,
            @RequestParam(name = "updated_at", required = false) String modifiedDate,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String task
    ) {
        LocalDate date = modifiedDate != null ? LocalDate.parse(modifiedDate) : null;
        return ResponseEntity.ok(scheduleService.getAllSchedules(id, date, null, null, title, task));
    }



    // 사용자 수정
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequestDto requestDto) {
        return ResponseEntity.ok(userService.updateUser(id, requestDto));
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @Valid @RequestBody UserRequestDto requestDto) {
        userService.deleteUser(id, requestDto);
        return ResponseEntity.ok("사용자 삭제 완료");
    }

}
