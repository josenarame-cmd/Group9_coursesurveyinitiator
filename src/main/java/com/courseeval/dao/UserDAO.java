package com.courseeval.dao;

import com.courseeval.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() { return jdbcTemplate; }

    private RowMapper<User> userMapper = (rs, rowNum) -> {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setFullName(rs.getString("full_name"));
        u.setRoleId(rs.getInt("role_id"));
        u.setStatus(rs.getString("status"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        try { u.setRoleName(rs.getString("role_name")); } catch (Exception ignored) {}
        return u;
    };

    public User findByUsername(String username) {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.username = ?";
        List<User> users = jdbcTemplate.query(sql, userMapper, username);
        return users.isEmpty() ? null : users.get(0);
    }

    public User findById(int userId) {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE u.user_id = ?";
        List<User> users = jdbcTemplate.query(sql, userMapper, userId);
        return users.isEmpty() ? null : users.get(0);
    }

    public List<User> findAll() {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id ORDER BY u.created_at DESC";
        return jdbcTemplate.query(sql, userMapper);
    }

    public List<User> findByRole(String roleName) {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE r.role_name = ? ORDER BY u.full_name";
        return jdbcTemplate.query(sql, userMapper, roleName);
    }

    public List<User> findPendingTeachers() {
        String sql = "SELECT u.*, r.role_name FROM users u JOIN roles r ON u.role_id = r.role_id WHERE r.role_name = 'TEACHER' AND u.status = 'PENDING'";
        return jdbcTemplate.query(sql, userMapper);
    }

    public void save(User user) {
        String sql = "INSERT INTO users (username, email, password, full_name, role_id, status) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getFullName(), user.getRoleId(), user.getStatus());
    }

    // --- NEW: Update User without changing password ---
    public void update(User user) {
        String sql = "UPDATE users SET username=?, email=?, full_name=?, role_id=?, status=? WHERE user_id=?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getFullName(), user.getRoleId(), user.getStatus(), user.getUserId());
    }

    // --- NEW: Update User and change password ---
    public void updateWithPassword(User user) {
        String sql = "UPDATE users SET username=?, email=?, password=?, full_name=?, role_id=?, status=? WHERE user_id=?";
        jdbcTemplate.update(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getFullName(), user.getRoleId(), user.getStatus(), user.getUserId());
    }

    // --- NEW: Delete User ---
    public void delete(int userId) {
        jdbcTemplate.update("DELETE FROM users WHERE user_id = ?", userId);
    }

    public void updateStatus(int userId, String status) {
        jdbcTemplate.update("UPDATE users SET status = ? WHERE user_id = ?", status, userId);
    }

    public boolean existsByUsername(String username) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE username = ?", Integer.class, username);
        return count != null && count > 0;
    }

    public boolean existsByEmail(String email) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users WHERE email = ?", Integer.class, email);
        return count != null && count > 0;
    }
}