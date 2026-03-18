package com.courseeval.controller;

import com.courseeval.dao.CourseDAO;
import com.courseeval.dao.SurveyDAO;
import com.courseeval.dao.UserDAO;
import com.courseeval.model.Course;
import com.courseeval.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserDAO userDAO;
    @Autowired private CourseDAO courseDAO;
    @Autowired private SurveyDAO surveyDAO;


    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        model.addAttribute("pendingTeachers", userDAO.findPendingTeachers());
        model.addAttribute("totalCourses", courseDAO.findAll().size());
        model.addAttribute("totalSurveys", surveyDAO.findAll().size());
        return "admin/dashboard";
    }

    // ---- Teacher approvals ----
    @GetMapping("/teachers")
    public String teachers(HttpSession session, Model model) {
        model.addAttribute("teachers", userDAO.findByRole("TEACHER"));
        return "admin/teachers";
    }

    @PostMapping("/teachers/approve")
    public String approveTeacher(@RequestParam int userId, RedirectAttributes ra) {
        userDAO.updateStatus(userId, "ACTIVE");
        ra.addFlashAttribute("success", "Teacher approved.");
        return "redirect:/admin/teachers";
    }

    @PostMapping("/teachers/reject")
    public String rejectTeacher(@RequestParam int userId, RedirectAttributes ra) {
        userDAO.updateStatus(userId, "REJECTED");
        ra.addFlashAttribute("success", "Teacher rejected.");
        return "redirect:/admin/teachers";
    }

    // ---- Course management ----
    @GetMapping("/courses")
    public String courses(HttpSession session, Model model) {
        model.addAttribute("courses", courseDAO.findAll());
        model.addAttribute("course", new Course());
        return "admin/courses";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course, HttpSession session,
                             RedirectAttributes ra) {
        User admin = (User) session.getAttribute("loggedUser");
        course.setCreatedBy(admin.getUserId());
        if (course.getCourseId() == 0) courseDAO.save(course);
        else courseDAO.update(course);
        ra.addFlashAttribute("success", "Course saved.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable int id, RedirectAttributes ra) {
        courseDAO.delete(id);
        ra.addFlashAttribute("success", "Course deleted.");
        return "redirect:/admin/courses";
    }

    // ---- Teacher assignment ----
    @GetMapping("/courses/assign/{courseId}")
    public String assignPage(@PathVariable int courseId, HttpSession session, Model model) {
        model.addAttribute("course", courseDAO.findById(courseId));
        model.addAttribute("allTeachers", userDAO.findByRole("TEACHER"));
        return "admin/assign-teacher";
    }

    @PostMapping("/courses/assign")
    public String doAssign(@RequestParam int teacherId, @RequestParam int courseId,
                           RedirectAttributes ra) {
        courseDAO.assignTeacher(teacherId, courseId);
        ra.addFlashAttribute("success", "Teacher assigned.");
        return "redirect:/admin/courses";
    }

    // ---- All surveys view ----
    @GetMapping("/surveys")
    public String surveys(HttpSession session, Model model) {
        model.addAttribute("surveys", surveyDAO.findAll());
        return "admin/surveys";
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (session.getAttribute("loggedUser") == null) return "redirect:/login";
        model.addAttribute("users", userDAO.findAll());
        return "admin/users";
    }
}
