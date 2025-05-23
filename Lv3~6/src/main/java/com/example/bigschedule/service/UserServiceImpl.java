package com.example.bigschedule.service;

import com.example.bigschedule.dto.AuthDto;
import com.example.bigschedule.dto.UserRequestDto;
import com.example.bigschedule.dto.UserResponseDto;
import com.example.bigschedule.dto.UserUpdateRequestDto;
import com.example.bigschedule.entity.User;
import com.example.bigschedule.exception.CustomException;
import com.example.bigschedule.exception.ErrorCode;
import com.example.bigschedule.repository.UserJdbcRepository;
import com.example.bigschedule.validation.OwnershipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserJdbcRepository userJdbcRepository;
    private final OwnershipValidator ownershipValidator;

    @Transactional
    @Override
    public UserResponseDto createUser(UserRequestDto requestDto) {
        if (userJdbcRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
            //이메일은 고유한 KEY여야 하기 때문에 중복되면 안됨.
        }
        User user = new User();
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userJdbcRepository.save(user);
        return toDto(user);
    }


    @Override
    public UserResponseDto getUserById(Long id) {
        return toDto(userJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userJdbcRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateRequestDto requestDto) {
        // 1. 기존 유저 조회
        User user = userJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 2. 소유권 및 유효성 검증
        ownershipValidator.validate(user, requestDto.getAuth().getEmail(),
                requestDto.getAuth().getName(), requestDto.getAuth().getPassword());

        // 3. 변경할 정보 세팅
        user.setName(requestDto.getName());
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setUpdatedAt(LocalDateTime.now());  // 업데이트 시간도 갱신

        // 4. DB 업데이트 호출
        userJdbcRepository.update(user);

        // 5. 변경된 유저 정보 DTO 변환 후 반환
        return toDto(user);
    }

    @Override
    public void deleteUser(Long id, UserRequestDto dto) {
        User user = userJdbcRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ownershipValidator.validate(user, dto.getEmail(), dto.getName(), dto.getPassword());

        userJdbcRepository.deleteById(id);
    }


    private UserResponseDto toDto(User user) {
        return new UserResponseDto(user);
    }

    @Override
    public User findUserByAuth(AuthDto authDto) {
        User user = userJdbcRepository.findByEmail(authDto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ownershipValidator.validate(user, authDto.getEmail(), authDto.getName(), authDto.getPassword());
        return user;
    }

}
