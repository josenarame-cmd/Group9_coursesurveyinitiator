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

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/survey")
public class RespondentController {

    @Autowired private SurveyDAO surveyDAO;
    @Autowired private UserDAO userDAO;
    @Autowired private EmailService emailService;

    // ---- List published surveys ----
    @GetMapping("/list")
    public String listSurveys(HttpSession session, Model model) {
        model.addAttribute("surveys", surveyDAO.findPublished());
        model.addAttribute("loggedUser", session.getAttribute("loggedUser"));
        return "respondent/survey-list";
    }

    // ---- Take a survey ----
    @GetMapping("/{id}/take")
    public String takeSurvey(@PathVariable int id, HttpSession session, Model model,
                             RedirectAttributes ra) {
        Survey survey = surveyDAO.findById(id);
        if (survey == null || !"PUBLISHED".equals(survey.getStatus())) {
            ra.addFlashAttribute("error", "Survey not available.");
            return "redirect:/survey/list";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        // Check access type
        if ("AUTHENTICATED".equals(survey.getAccessType()) && loggedUser == null) {
            ra.addFlashAttribute("error", "You must be logged in to take this survey.");
            return "redirect:/login";
        }

        // Check if already submitted (authenticated)
        if (loggedUser != null) {
            // find respondent linked to this user
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

    // ---- Submit survey ----
    @PostMapping("/{id}/submit")
    public String submitSurvey(@PathVariable int id,
                               @RequestParam Map<String, String> params,
                               @RequestParam(required = false) String guestEmail,
                               HttpSession session,
                               RedirectAttributes ra) {
        Survey survey = surveyDAO.findById(id);
        User loggedUser = (User) session.getAttribute("loggedUser");

        // Create / find respondent
        int respondentId;
        String emailToNotify = null;

        if (loggedUser != null) {
            respondentId = ensureRespondent(loggedUser.getUserId(),
                    loggedUser.getFullName(), loggedUser.getEmail());
            emailToNotify = loggedUser.getEmail();
        } else {
            // Guest
            if (guestEmail == null || guestEmail.trim().isEmpty()) {
                ra.addFlashAttribute("error", "Please provide your email.");
                return "redirect:/survey/" + id + "/take";
            }
            respondentId = ensureGuestRespondent(guestEmail.trim());
            emailToNotify = guestEmail.trim();
        }

        // Save response header
        int responseId = surveyDAO.saveResponse(id, respondentId);

        // Save each answer
        List<SurveyQuestion> questions = surveyDAO.findQuestionsBySurvey(id);
        for (SurveyQuestion q : questions) {
            String key = "q_" + q.getQuestionId();
            String val = params.get(key);
            if (val != null && !val.isEmpty()) {
                if ("TEXT".equals(q.getQuestionType())) {
                    surveyDAO.saveAnswer(responseId, q.getQuestionId(), null, val);
                } else {
                    surveyDAO.saveAnswer(responseId, q.getQuestionId(),
                            Integer.parseInt(val), null);
                }
            }
        }

        // Send confirmation email
        if (emailToNotify != null && survey != null) {
            emailService.sendConfirmation(emailToNotify,
                    "Survey Submission Confirmed: " + survey.getTitle(),
                    "Thank you for completing the survey: " + survey.getTitle() +
                            ".\n\nYour response has been recorded.");
        }

        ra.addFlashAttribute("success", "Thank you! Your response has been submitted.");
        return "redirect:/survey/list";
    }

    // ---- Helper: get or create respondent for authenticated user ----
    private Integer getRespondentIdForUser(int userId) {
        try {
            return userDAO.getJdbcTemplate().queryForObject(
                    "SELECT respondent_id FROM respondents WHERE user_id = ?",
                    Integer.class, userId);
        } catch (Exception e) {
            return null;
        }
    }

    private int ensureRespondent(int userId, String name, String email) {
        Integer existing = getRespondentIdForUser(userId);
        if (existing != null) return existing;

        userDAO.getJdbcTemplate().update(
                "INSERT INTO respondents (user_id, guest_email, display_name) VALUES (?,?,?)",
                userId, email, name);

        // Corrected: Fetch it right back using the user_id instead of LAST_INSERT_ID
        return getRespondentIdForUser(userId);
    }

    private int ensureGuestRespondent(String email) {
        try {
            // Corrected: Added LIMIT 1 to prevent multiple results error
            return userDAO.getJdbcTemplate().queryForObject(
                    "SELECT respondent_id FROM respondents WHERE guest_email = ? AND user_id IS NULL LIMIT 1",
                    Integer.class, email);
        } catch (Exception e) {
            userDAO.getJdbcTemplate().update(
                    "INSERT INTO respondents (user_id, guest_email, display_name) VALUES (NULL, ?, ?)",
                    email, email);

            // Corrected: Fetch it right back using the email instead of LAST_INSERT_ID
            return userDAO.getJdbcTemplate().queryForObject(
                    "SELECT respondent_id FROM respondents WHERE guest_email = ? AND user_id IS NULL LIMIT 1",
                    Integer.class, email);
        }
    }
}