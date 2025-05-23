package com.example.bigschedule.validation;

import com.example.bigschedule.entity.Schedule;
import com.example.bigschedule.entity.User;
import com.example.bigschedule.exception.CustomException;
import com.example.bigschedule.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class OwnershipValidator {

    public void validate(User user, String email, String name, String password) {
        if (!user.getEmail().equals(email)) {
            throw new CustomException(ErrorCode.EMAIL_MISMATCH);
        }
        if (!user.getName().equals(name)) {
            throw new CustomException(ErrorCode.NAME_MISMATCH);
        }
        if (!user.getPassword().equals(password)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

    public void validate(Schedule schedule, String email, String name, String password) {
        validate(schedule.getUser(), email, name, password);
    }
}