package com.example.schedule.repository;

import com.example.schedule.dto.ScheduleResponseDto;
import com.example.schedule.entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ScheduleJdbcRepository implements ScheduleRepository {
    private final JdbcTemplate jdbcTemplate;

    public ScheduleJdbcRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public ScheduleResponseDto save(Schedule schedule) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("schedule")
                .usingGeneratedKeyColumns("id");

        Map<String, Object> params = new HashMap<>();
        params.put("title", schedule.getTitle());
        params.put("task", schedule.getTask());
        params.put("author", schedule.getAuthor());
        params.put("password", schedule.getPassword());
        params.put("created_at", schedule.getCreatedAt());
        params.put("updated_at", schedule.getUpdatedAt());

        Number key = insert.executeAndReturnKey(params);

        return new ScheduleResponseDto(
                key.longValue(),
                schedule.getTitle(),
                schedule.getTask(),
                schedule.getAuthor(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Override
    public List<ScheduleResponseDto> findAllByModifiedDateAndWriter(LocalDate modifiedDate, String writer) {
        StringBuilder sql = new StringBuilder("SELECT * FROM schedule WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (modifiedDate != null) {
            sql.append("AND DATE(updated_at) = ? ");
            params.add(modifiedDate);
        }

        if (writer != null && !writer.isEmpty()) {
            sql.append("AND author = ? ");
            params.add(writer);
        }

        sql.append("ORDER BY updated_at DESC");

        return jdbcTemplate.query(sql.toString(), scheduleRowMapper(), params.toArray());
    }

    @Override
    public Optional<Schedule> findById(Long id) {
        List<Schedule> result = jdbcTemplate.query(
                "SELECT * FROM schedule WHERE id = ?",
                scheduleEntityRowMapper(),
                id);

        return result.stream().findFirst();
    }

    @Override
    public int update(Long id, String todo, String writer, String password) {
        // 비밀번호 체크 먼저
        Optional<Schedule> scheduleOpt = findById(id);
        if (scheduleOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
        }

        Schedule schedule = scheduleOpt.get();
        if (!schedule.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password does not match");
        }

        return jdbcTemplate.update(
                "UPDATE schedule SET task = ?, author = ?, updated_at = ? WHERE id = ?",
                todo, writer, LocalDateTime.now(), id);
    }

    @Override
    public int delete(Long id, String password) {
        Optional<Schedule> scheduleOpt = findById(id);
        if (scheduleOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found");
        }

        Schedule schedule = scheduleOpt.get();
        if (!schedule.getPassword().equals(password)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Password does not match");
        }

        return jdbcTemplate.update("DELETE FROM schedule WHERE id = ?", id);
    }

    private RowMapper<ScheduleResponseDto> scheduleRowMapper() {
        return (rs, rowNum) -> new ScheduleResponseDto(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("task"),
                rs.getString("author"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }

    private RowMapper<Schedule> scheduleEntityRowMapper() {
        return (rs, rowNum) -> new Schedule(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("task"),
                rs.getString("author"),
                rs.getString("password"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()
        );
    }

}