package com.courseeval.controller;

import com.courseeval.dao.CourseDAO;
import com.courseeval.dao.SurveyDAO;
import com.courseeval.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired private SurveyDAO surveyDAO;
    @Autowired private CourseDAO courseDAO;
    @Autowired private JdbcTemplate jdbcTemplate; // Injected for fetching text answers safely

    private User requireTeacher(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        if (u == null || !"TEACHER".equals(u.getRoleName())) return null;
        return u;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User u = requireTeacher(session);
        if (u == null) return "redirect:/login";
        model.addAttribute("surveys", surveyDAO.findByTeacher(u.getUserId()));
        model.addAttribute("courses", courseDAO.findByTeacher(u.getUserId()));
        return "teacher/dashboard";
    }

    @GetMapping("/surveys/{id}/results")
    public String results(@PathVariable int id, HttpSession session, Model model) {
        User u = requireTeacher(session);
        if (u == null) return "redirect:/login";
        Survey survey = surveyDAO.findById(id);
        List<SurveyQuestion> questions = surveyDAO.findQuestionsBySurvey(id);

        for (SurveyQuestion q : questions) {
            if ("TEXT".equals(q.getQuestionType())) {
                // Fetch text answers dynamically
                List<String> texts = jdbcTemplate.queryForList(
                        "SELECT text_answer FROM response_answers WHERE question_id = ? AND text_answer IS NOT NULL AND text_answer != ''",
                        String.class, q.getQuestionId()
                );
                q.setTextResponses(texts);
            } else {
                List<SurveyOption> opts = surveyDAO.findOptionsByQuestion(q.getQuestionId());
                for (SurveyOption o : opts) {
                    o.setResponseCount(surveyDAO.getOptionCount(o.getOptionId()));
                }
                q.setOptions(opts);
            }
        }

        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        model.addAttribute("totalResponses", surveyDAO.getTotalResponses(id));
        return "teacher/results";
    }
}