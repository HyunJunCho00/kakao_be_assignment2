package com.example.bigschedule.repository;

import com.example.bigschedule.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    List<User> findAll();

    void save(User user);
    void update(User user);
    void deleteById(Long id);
}
