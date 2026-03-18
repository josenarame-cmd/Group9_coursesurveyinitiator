package com.courseeval.controller;

import com.courseeval.dao.CourseDAO;
import com.courseeval.dao.SurveyDAO;
import com.courseeval.model.*;
import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User u = (User) session.getAttribute("loggedUser");
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
            List<SurveyOption> opts = surveyDAO.findOptionsByQuestion(q.getQuestionId());
            for (SurveyOption o : opts) {
                o.setResponseCount(surveyDAO.getOptionCount(o.getOptionId()));
            }
            q.setOptions(opts);
        }
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        model.addAttribute("totalResponses", surveyDAO.getTotalResponses(id));
        return "teacher/results";
    }
}
