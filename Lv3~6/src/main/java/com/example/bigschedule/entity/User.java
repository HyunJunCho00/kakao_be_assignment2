package com.example.bigschedule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    //작성자가 등록한 일정이 여러 개일수도 있지 않을까?
    @OneToMany(mappedBy="user", cascade = CascadeType.REMOVE)
    // 처음에 JDBC로 구현하지 않고 JPA로 구현해서 동작했지만, JDBC의 경우는 동작하지 않음.
    // 직접 SQL문을 통해 동작하기 떄문에..

    private List<Schedule> schedules=new ArrayList<>();
}

