
package com.example.bigschedule.repository;

import com.example.bigschedule.entity.Schedule;
import com.example.bigschedule.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ScheduleJdbcRepository implements ScheduleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Schedule> scheduleEntityRowMapper() {
        return (rs, rowNum) -> {
            Schedule schedule = new Schedule();
            schedule.setId(rs.getLong("id"));
            schedule.setTitle(rs.getString("title"));
            schedule.setTask(rs.getString("task"));
            schedule.setCreatedAt(rs.getTimestamp("created_at") != null ?
                    rs.getTimestamp("created_at").toLocalDateTime() : null);
            schedule.setUpdatedAt(rs.getTimestamp("updated_at") != null ?
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);

            Long userId = rs.getLong("user_id");
            User user = new User();
            user.setId(userId);
            user.setName(rs.getString("user_name"));
            user.setEmail(rs.getString("user_email"));
            user.setPassword(rs.getString("password"));
            schedule.setUser(user);

            return schedule;
        };
    }


    //////////////// 저장 (insert or update)   ////////////////////////
    public Schedule saveSchedule(Schedule schedule) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "INSERT INTO schedule (title, task, user_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, schedule.getTitle(), schedule.getTask(), schedule.getUser().getId(), now, now);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        schedule.setId(id);
        schedule.setCreatedAt(now);
        schedule.setUpdatedAt(now);
        return schedule;
    }

    /////////////////////////////   삭제   ///////////////////////



    public void delete(Schedule schedule) {
        String sql = "DELETE FROM schedule WHERE id = ?";
        jdbcTemplate.update(sql, schedule.getId());
    }


    //////////////////////////   조회 부분   ///////////////////////////

    // 일정 단건 조회
    public Optional<Schedule> findById(Long id) {
        String sql = "SELECT s.*, u.name as user_name, u.email as user_email, u.password " +
                "FROM schedule s JOIN users u on s.user_id=u.id WHERE s.id = ?";
        List<Schedule> result = jdbcTemplate.query(sql, scheduleEntityRowMapper(), id);
        return result.stream().findFirst();
    }

    ////////////////////// 전체 조회   /////////////////////////
    public List<Schedule> findSchedules(
            Long userId, LocalDate modifiedDate, String name, String email, String title, String task
    ) {
        StringBuilder sql = new StringBuilder("SELECT s.*, u.name as user_name, u.email as user_email, u.password " +
                "FROM schedule s JOIN users u ON s.user_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND s.user_id = ?");
            params.add(userId);
        }
        if (modifiedDate != null) {
            sql.append(" AND DATE(s.updated_at) = ?");
            params.add(modifiedDate);
        }
        if (name != null && !name.isEmpty()) {
            sql.append(" AND u.name LIKE ?");
            params.add("%" + name + "%");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND u.email LIKE ?");
            params.add("%" + email + "%");
        }
        if (title != null && !title.isEmpty()) {
            sql.append(" AND s.title LIKE ?");
            params.add("%" + title + "%");
        }
        if (task != null && !task.isEmpty()) {
            sql.append(" AND s.task LIKE ?");
            params.add("%" + task + "%");
        }

        sql.append(" ORDER BY s.updated_at DESC");

        return jdbcTemplate.query(sql.toString(), scheduleEntityRowMapper(), params.toArray());
    }


    //////////////////////////Update 부분///////////////////////////

    public void updateScheduleFields(Long id, Map<String, Object> fieldsToUpdate) {
        if (fieldsToUpdate == null || fieldsToUpdate.isEmpty()) return;

        StringBuilder sql = new StringBuilder("UPDATE schedule SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            sql.append(entry.getKey()).append(" = ?, ");
            params.add(entry.getValue());
        }
        // 마지막 콤마와 공백 제거
        sql.setLength(sql.length() - 2);

        sql.append(", updated_at = ? WHERE id = ?");
        params.add(LocalDateTime.now());
        params.add(id);


        jdbcTemplate.update(sql.toString(), params.toArray());
    }




    ////////////////////////// 페이지네이션은 offset, limit 사용  ///////////////

    public List<Schedule> findAllPagedWithUserName(int offset, int limit)
    {
        String sql="SELECT s.*, u.name AS user_name, u.email AS user_email, u.password " +
                "FROM schedule.schedule s " +
                "JOIN schedule.users u ON s.user_id = u.id " +
                "ORDER BY s.updated_at DESC " +
                "LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, scheduleEntityRowMapper(), limit, offset);
    }


    public long countAll() {
        String sql = "SELECT COUNT(*) FROM schedule";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

}
