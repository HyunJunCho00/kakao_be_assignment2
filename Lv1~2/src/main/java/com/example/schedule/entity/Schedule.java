package com.example.schedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String task;
    private String author;
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

