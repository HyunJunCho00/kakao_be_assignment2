
package com.example.bigschedule.repository;

import com.example.bigschedule.entity.User;
import com.example.bigschedule.exception.CustomException;
import com.example.bigschedule.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserJdbcRepository implements UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<User> userEntityRowMapper() {
        return (rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            user.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return user;
        };
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPassword()
        );
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, userEntityRowMapper(), id);
        return users.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> users = jdbcTemplate.query(sql, userEntityRowMapper(), email);
        return users.stream().findFirst();
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, userEntityRowMapper());
    }

    public void deleteById(Long id) {
        // 1. 해당 사용자의 스케줄 먼저 삭제
        String deleteSchedulesSql = "DELETE FROM schedule WHERE user_id = ?";
        jdbcTemplate.update(deleteSchedulesSql,id);

        // 2. 사용자 삭제
        String deleteUserSql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(deleteUserSql,id);

    }

    public void update(User user) {
        // 이메일 중복 체크 먼저 수행 필요
        if (isEmailDuplicate(user.getEmail(), user.getId())) {
            throw new CustomException(ErrorCode.DUPLICATE_USER);
        }

        String sql = "UPDATE users SET name = ?, email = ?, password = ?, updated_at = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                user.getUpdatedAt(),
                user.getId()
        );
    }
    private boolean isEmailDuplicate(String email, Long userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email, userId);
        return count != null && count > 0;
    }


}
