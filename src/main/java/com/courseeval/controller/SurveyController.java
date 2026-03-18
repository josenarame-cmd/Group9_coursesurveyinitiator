package com.courseeval.controller;

import com.courseeval.dao.CourseDAO;
import com.courseeval.dao.SurveyDAO;
import com.courseeval.model.*;
import com.courseeval.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/initiator")
public class SurveyController {

    @Autowired private SurveyDAO surveyDAO;
    @Autowired private CourseDAO courseDAO;
    @Autowired private EmailService emailService;
    @Autowired private JdbcTemplate jdbcTemplate; // Injected for fetching text answers safely

    private User requireInitiator(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        if (u == null || !"INITIATOR".equals(u.getRoleName())) return null;
        return u;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        model.addAttribute("surveys", surveyDAO.findByCreator(u.getUserId()));
        return "initiator/dashboard";
    }

    // ---- Create survey ----
    @GetMapping("/surveys/new")
    public String newSurvey(HttpSession session, Model model) {
        if (requireInitiator(session) == null) return "redirect:/login";
        model.addAttribute("survey", new Survey());
        model.addAttribute("courses", courseDAO.findAll());
        return "initiator/survey-form";
    }

    @PostMapping("/surveys/save")
    public String saveSurvey(@ModelAttribute Survey survey, HttpSession session,
                             RedirectAttributes ra) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        survey.setCreatedBy(u.getUserId());
        if (survey.getSurveyId() == 0) {
            survey.setStatus("DRAFT");
            int id = surveyDAO.save(survey);
            ra.addFlashAttribute("success", "Survey created. Now add questions.");
            return "redirect:/initiator/surveys/" + id + "/questions";
        } else {
            surveyDAO.update(survey);
            ra.addFlashAttribute("success", "Survey updated.");
            return "redirect:/initiator/dashboard";
        }
    }

    // ---- Edit survey ----
    @GetMapping("/surveys/{id}/edit")
    public String editSurvey(@PathVariable int id, HttpSession session, Model model) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        Survey survey = surveyDAO.findById(id);
        if (survey == null || survey.getCreatedBy() != u.getUserId())
            return "redirect:/initiator/dashboard";
        model.addAttribute("survey", survey);
        model.addAttribute("courses", courseDAO.findAll());
        return "initiator/survey-form";
    }

    // ---- Manage questions ----
    @GetMapping("/surveys/{id}/questions")
    public String manageQuestions(@PathVariable int id, HttpSession session, Model model) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        Survey survey = surveyDAO.findById(id);
        List<SurveyQuestion> questions = surveyDAO.findQuestionsBySurvey(id);
        for (SurveyQuestion q : questions) {
            q.setOptions(surveyDAO.findOptionsByQuestion(q.getQuestionId()));
        }
        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        return "initiator/questions";
    }

    @PostMapping("/surveys/{id}/questions/add")
    public String addQuestion(@PathVariable int id,
                              @RequestParam String questionText,
                              @RequestParam String questionType,
                              @RequestParam(required = false) List<String> options) {
        SurveyQuestion q = new SurveyQuestion();
        q.setSurveyId(id);
        q.setQuestionText(questionText);
        q.setQuestionType(questionType);
        int qId = surveyDAO.saveQuestion(q);

        // Fix: Make sure it saves options for MULTIPLE_CHOICE and SINGLE_CHOICE
        if (options != null && ("SINGLE_CHOICE".equals(questionType) || "MULTIPLE_CHOICE".equals(questionType))) {
            for (int i = 0; i < options.size(); i++) {
                if (!options.get(i).trim().isEmpty()) {
                    SurveyOption o = new SurveyOption();
                    o.setQuestionId(qId);
                    o.setOptionText(options.get(i).trim());
                    o.setOrderNum(i);
                    surveyDAO.saveOption(o);
                }
            }
        }
        return "redirect:/initiator/surveys/" + id + "/questions";
    }

    @GetMapping("/surveys/{surveyId}/questions/{qId}/delete")
    public String deleteQuestion(@PathVariable int surveyId, @PathVariable int qId) {
        surveyDAO.deleteQuestion(qId);
        return "redirect:/initiator/surveys/" + surveyId + "/questions";
    }

    // ---- Publish / close ----
    @PostMapping("/surveys/{id}/publish")
    public String publish(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        Survey s = surveyDAO.findById(id);
        s.setStatus("PUBLISHED");
        surveyDAO.update(s);
        ra.addFlashAttribute("success", "Survey published.");
        return "redirect:/initiator/dashboard";
    }

    @PostMapping("/surveys/{id}/close")
    public String close(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        Survey s = surveyDAO.findById(id);
        s.setStatus("CLOSED");
        surveyDAO.update(s);
        ra.addFlashAttribute("success", "Survey closed.");
        return "redirect:/initiator/dashboard";
    }

    // ---- Delete survey ----
    @GetMapping("/surveys/{id}/delete")
    public String deleteSurvey(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        User u = requireInitiator(session);
        if (u == null) return "redirect:/login";
        surveyDAO.delete(id);
        ra.addFlashAttribute("success", "Survey deleted.");
        return "redirect:/initiator/dashboard";
    }

    // ---- Results ----
    @GetMapping("/surveys/{id}/results")
    public String results(@PathVariable int id, HttpSession session, Model model) {
        if (requireInitiator(session) == null) return "redirect:/login";
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
        return "initiator/results";
    }
}