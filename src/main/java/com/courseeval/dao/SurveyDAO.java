package com.courseeval.dao;

import com.courseeval.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class SurveyDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Survey> surveyMapper = (rs, rn) -> {
        Survey s = new Survey();
        s.setSurveyId(rs.getInt("survey_id"));
        s.setTitle(rs.getString("title"));
        s.setDescription(rs.getString("description"));
        s.setCourseId(rs.getInt("course_id"));
        s.setCreatedBy(rs.getInt("created_by"));
        s.setAccessType(rs.getString("access_type"));
        s.setStatus(rs.getString("status"));
        s.setStartDate(rs.getDate("start_date"));
        s.setEndDate(rs.getDate("end_date"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        try { s.setCourseName(rs.getString("course_name")); } catch (Exception ignored) {}
        try { s.setCreatorName(rs.getString("full_name")); } catch (Exception ignored) {}
        return s;
    };

    private RowMapper<SurveyQuestion> questionMapper = (rs, rn) -> {
        SurveyQuestion q = new SurveyQuestion();
        q.setQuestionId(rs.getInt("question_id"));
        q.setSurveyId(rs.getInt("survey_id"));
        q.setQuestionText(rs.getString("question_text"));
        q.setQuestionType(rs.getString("question_type"));
        q.setOrderNum(rs.getInt("order_num"));
        return q;
    };

    private RowMapper<SurveyOption> optionMapper = (rs, rn) -> {
        SurveyOption o = new SurveyOption();
        o.setOptionId(rs.getInt("option_id"));
        o.setQuestionId(rs.getInt("question_id"));
        o.setOptionText(rs.getString("option_text"));
        o.setOrderNum(rs.getInt("order_num"));
        return o;
    };

    // ---- Survey CRUD ----

    public List<Survey> findAll() {
        String sql = "SELECT s.*, c.course_name, u.full_name FROM surveys s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN users u ON s.created_by = u.user_id " +
                     "ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, surveyMapper);
    }

    public List<Survey> findByCreator(int userId) {
        String sql = "SELECT s.*, c.course_name, u.full_name FROM surveys s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.created_by = ? ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, surveyMapper, userId);
    }

    public List<Survey> findByTeacher(int teacherId) {
        String sql = "SELECT s.*, c.course_name, u.full_name FROM surveys s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN teacher_courses tc ON c.course_id = tc.course_id " +
                     "JOIN users u ON s.created_by = u.user_id " +
                     "WHERE tc.teacher_id = ? ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, surveyMapper, teacherId);
    }

    public List<Survey> findPublished() {
        String sql = "SELECT s.*, c.course_name, u.full_name FROM surveys s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.status = 'PUBLISHED' ORDER BY s.created_at DESC";
        return jdbcTemplate.query(sql, surveyMapper);
    }

    public Survey findById(int surveyId) {
        String sql = "SELECT s.*, c.course_name, u.full_name FROM surveys s " +
                     "JOIN courses c ON s.course_id = c.course_id " +
                     "JOIN users u ON s.created_by = u.user_id " +
                     "WHERE s.survey_id = ?";
        List<Survey> list = jdbcTemplate.query(sql, surveyMapper, surveyId);
        return list.isEmpty() ? null : list.get(0);
    }

    public int save(Survey survey) {
        String sql = "INSERT INTO surveys (title, description, course_id, created_by, " +
                     "access_type, status, start_date, end_date) VALUES (?,?,?,?,?,?,?,?)";
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, survey.getTitle());
            ps.setString(2, survey.getDescription());
            ps.setInt(3, survey.getCourseId());
            ps.setInt(4, survey.getCreatedBy());
            ps.setString(5, survey.getAccessType());
            ps.setString(6, survey.getStatus());
            ps.setDate(7, survey.getStartDate());
            ps.setDate(8, survey.getEndDate());
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    public void update(Survey survey) {
        jdbcTemplate.update(
            "UPDATE surveys SET title=?, description=?, course_id=?, access_type=?, " +
            "status=?, start_date=?, end_date=? WHERE survey_id=?",
            survey.getTitle(), survey.getDescription(), survey.getCourseId(),
            survey.getAccessType(), survey.getStatus(),
            survey.getStartDate(), survey.getEndDate(), survey.getSurveyId());
    }

    public void delete(int surveyId) {
        jdbcTemplate.update("DELETE FROM surveys WHERE survey_id = ?", surveyId);
    }

    // ---- Questions ----

    public List<SurveyQuestion> findQuestionsBySurvey(int surveyId) {
        return jdbcTemplate.query(
            "SELECT * FROM survey_questions WHERE survey_id = ? ORDER BY order_num",
            questionMapper, surveyId);
    }

    public int saveQuestion(SurveyQuestion q) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO survey_questions (survey_id, question_text, question_type, order_num) VALUES (?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, q.getSurveyId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getQuestionType());
            ps.setInt(4, q.getOrderNum());
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    public void deleteQuestion(int questionId) {
        jdbcTemplate.update("DELETE FROM survey_questions WHERE question_id = ?", questionId);
    }

    // ---- Options ----

    public List<SurveyOption> findOptionsByQuestion(int questionId) {
        return jdbcTemplate.query(
            "SELECT * FROM survey_options WHERE question_id = ? ORDER BY order_num",
            optionMapper, questionId);
    }

    public void saveOption(SurveyOption o) {
        jdbcTemplate.update(
            "INSERT INTO survey_options (question_id, option_text, order_num) VALUES (?,?,?)",
            o.getQuestionId(), o.getOptionText(), o.getOrderNum());
    }

    // ---- Responses ----

    public boolean hasResponded(int surveyId, int respondentId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM survey_responses WHERE survey_id=? AND respondent_id=?",
            Integer.class, surveyId, respondentId);
        return count != null && count > 0;
    }

    public int saveResponse(int surveyId, int respondentId) {
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO survey_responses (survey_id, respondent_id) VALUES (?,?)",
                Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, surveyId);
            ps.setInt(2, respondentId);
            return ps;
        }, kh);
        return kh.getKey().intValue();
    }

    public void saveAnswer(int responseId, int questionId, Integer optionId, String textAnswer) {
        jdbcTemplate.update(
            "INSERT INTO response_answers (response_id, question_id, option_id, text_answer) VALUES (?,?,?,?)",
            responseId, questionId, optionId, textAnswer);
    }

    public int getTotalResponses(int surveyId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM survey_responses WHERE survey_id=?", Integer.class, surveyId);
        return count == null ? 0 : count;
    }

    public int getOptionCount(int optionId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM response_answers WHERE option_id=?", Integer.class, optionId);
        return count == null ? 0 : count;
    }
}
