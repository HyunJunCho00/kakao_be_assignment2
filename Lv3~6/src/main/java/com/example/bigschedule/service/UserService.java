package com.example.bigschedule.service;

import com.example.bigschedule.dto.AuthDto;
import com.example.bigschedule.dto.UserRequestDto;
import com.example.bigschedule.dto.UserResponseDto;
import com.example.bigschedule.dto.UserUpdateRequestDto;
import com.example.bigschedule.entity.User;

import java.util.List;

public interface UserService {
    UserResponseDto createUser(UserRequestDto requestDto);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto);
    void deleteUser(Long id,UserRequestDto requestDto);
    User findUserByAuth(AuthDto authDto);

}
