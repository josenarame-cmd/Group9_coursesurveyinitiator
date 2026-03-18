package com.courseeval.controller;

import com.courseeval.dao.SurveyDAO;
import com.courseeval.dao.UserDAO;
import com.courseeval.model.*;
import com.courseeval.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/survey")
public class RespondentController {

    @Autowired private SurveyDAO surveyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private EmailService emailService;

    @GetMapping("/list")
    public String listSurveys(HttpSession session, Model model) {
        model.addAttribute("surveys", surveyDAO.findPublished());
        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
        return "respondent/survey-list";
    }

    @GetMapping("/{id}/take")
    public String takeSurvey(@PathVariable int id, HttpSession session, Model model, RedirectAttributes ra) {
        Survey survey = surveyDAO.findById(id);
        if (survey == null || !"PUBLISHED".equals(survey.getStatus())) {
            ra.addFlashAttribute("error", "Survey not available.");
            return "redirect:/survey/list";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        if ("AUTHENTICATED".equals(survey.getAccessType()) && loggedUser == null) {
            ra.addFlashAttribute("error", "You must be logged in to take this survey.");
            return "redirect:/login";
        }

        if (loggedUser != null) {
            Integer respondentId = getRespondentIdForUser(loggedUser.getUserId());
            if (respondentId != null && surveyDAO.hasResponded(id, respondentId)) {
                ra.addFlashAttribute("error", "You have already submitted this survey.");
                return "redirect:/survey/list";
            }
        }

        List<SurveyQuestion> questions = surveyDAO.findQuestionsBySurvey(id);
        for (SurveyQuestion q : questions) {
            q.setOptions(surveyDAO.findOptionsByQuestion(q.getQuestionId()));
        }

        model.addAttribute("survey", survey);
        model.addAttribute("questions", questions);
        model.addAttribute("loggedUser", loggedUser);
        return "respondent/take-survey";
    }

    @PostMapping("/{id}/submit")
    public String submitSurvey(@PathVariable int id,
                               HttpServletRequest request,
                               @RequestParam(required = false) String guestEmail,
                               HttpSession session,
                               RedirectAttributes ra) {
        Survey survey = surveyDAO.findById(id);
        User loggedUser = (User) session.getAttribute("loggedUser");

        int respondentId;
        String emailToNotify = null;

        if (loggedUser != null) {
            respondentId = ensureRespondent(loggedUser.getUserId(), loggedUser.getFullName(), loggedUser.getEmail());
            emailToNotify = loggedUser.getEmail();
        } else {
            if (guestEmail == null || guestEmail.trim().isEmpty()) {
                ra.addFlashAttribute("error", "Please provide your email.");
                return "redirect:/survey/" + id + "/take";
            }
            respondentId = ensureGuestRespondent(guestEmail.trim());
            emailToNotify = guestEmail.trim();
        }

        if (surveyDAO.hasResponded(id, respondentId)) {
            ra.addFlashAttribute("error", "You have already submitted this survey.");
            return "redirect:/survey/list";
        }

        int responseId = surveyDAO.saveResponse(id, respondentId);

        List<SurveyQuestion> questions = surveyDAO.findQuestionsBySurvey(id);
        for (SurveyQuestion q : questions) {
            String key = "q_" + q.getQuestionId();
            String[] values = request.getParameterValues(key);

            if (values != null && values.length > 0) {
                if ("TEXT".equals(q.getQuestionType())) {
                    surveyDAO.saveAnswer(responseId, q.getQuestionId(), null, values[0]);
                } else {
                    for (String val : values) {
                        surveyDAO.saveAnswer(responseId, q.getQuestionId(), Integer.parseInt(val), null);
                    }
                }
            }
        }

        // Try to send the email and get the true/false result
        boolean emailSentSuccessfully = false;
        if (emailToNotify != null && emailToNotify.contains("@") && survey != null) {
            emailSentSuccessfully = emailService.sendConfirmation(emailToNotify,
                    "Survey Submission Confirmed: " + survey.getTitle(),
                    "Thank you for completing the survey: " + survey.getTitle() + ".\n\nYour response has been recorded.");
        }

        // Check the result and set the exact message!
        if (emailSentSuccessfully) {
            ra.addFlashAttribute("success", "Thank you! Survey submitted and confirmation email sent successfully to " + emailToNotify);
        } else {
            ra.addFlashAttribute("success", "Thank you! Survey submitted successfully.");
            ra.addFlashAttribute("error", "Warning: We could not send a confirmation email to " + emailToNotify + ". (System email configuration error or invalid email).");
        }

        return "redirect:/survey/list";
    }

    private Integer getRespondentIdForUser(int userId) {
        try {
            return userDAO.getJdbcTemplate().queryForObject(
                    "SELECT respondent_id FROM respondents WHERE user_id = ?", Integer.class, userId);
        } catch (Exception e) {
            return null;
        }
    }

    private int ensureRespondent(int userId, String name, String email) {
        Integer existing = getRespondentIdForUser(userId);
        if (existing != null) return existing;

        userDAO.getJdbcTemplate().update(
                "INSERT INTO respondents (user_id, guest_email, display_name) VALUES (?,?,?)", userId, email, name);
        return getRespondentIdForUser(userId);
    }

    private int ensureGuestRespondent(String email) {
        try {
            return userDAO.getJdbcTemplate().queryForObject(
                    "SELECT respondent_id FROM respondents WHERE guest_email = ? AND user_id IS NULL LIMIT 1", Integer.class, email);
        } catch (Exception e) {
            userDAO.getJdbcTemplate().update(
                    "INSERT INTO respondents (user_id, guest_email, display_name) VALUES (NULL, ?, ?)", email, email);
            return userDAO.getJdbcTemplate().queryForObject(
                    "SELECT respondent_id FROM respondents WHERE guest_email = ? AND user_id IS NULL LIMIT 1", Integer.class, email);
        }
    }
}