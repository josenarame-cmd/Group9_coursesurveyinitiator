package com.courseeval.dao;

import com.courseeval.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Course> courseMapper = (rs, rowNum) -> {
        Course c = new Course();
        c.setCourseId(rs.getInt("course_id"));
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDescription(rs.getString("description"));
        c.setCreatedBy(rs.getInt("created_by"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    };

    public List<Course> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM courses ORDER BY course_name", courseMapper);
    }

    public Course findById(int courseId) {
        List<Course> list = jdbcTemplate.query(
            "SELECT * FROM courses WHERE course_id = ?", courseMapper, courseId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Course> findByTeacher(int teacherId) {
        String sql = "SELECT c.* FROM courses c " +
                     "JOIN teacher_courses tc ON c.course_id = tc.course_id " +
                     "WHERE tc.teacher_id = ?";
        return jdbcTemplate.query(sql, courseMapper, teacherId);
    }

    public void save(Course course) {
        jdbcTemplate.update(
            "INSERT INTO courses (course_code, course_name, description, created_by) VALUES (?,?,?,?)",
            course.getCourseCode(), course.getCourseName(),
            course.getDescription(), course.getCreatedBy());
    }

    public void update(Course course) {
        jdbcTemplate.update(
            "UPDATE courses SET course_code=?, course_name=?, description=? WHERE course_id=?",
            course.getCourseCode(), course.getCourseName(),
            course.getDescription(), course.getCourseId());
    }

    public void delete(int courseId) {
        jdbcTemplate.update("DELETE FROM courses WHERE course_id = ?", courseId);
    }

    public void assignTeacher(int teacherId, int courseId) {
        jdbcTemplate.update(
            "INSERT IGNORE INTO teacher_courses (teacher_id, course_id) VALUES (?,?)",
            teacherId, courseId);
    }

    public void removeTeacher(int teacherId, int courseId) {
        jdbcTemplate.update(
            "DELETE FROM teacher_courses WHERE teacher_id=? AND course_id=?",
            teacherId, courseId);
    }
}
