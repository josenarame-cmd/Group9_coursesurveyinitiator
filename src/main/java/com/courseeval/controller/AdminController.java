package com.courseeval.controller;

import com.courseeval.dao.CourseDAO;
import com.courseeval.dao.SurveyDAO;
import com.courseeval.dao.UserDAO;
import com.courseeval.model.Course;
import com.courseeval.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    @Autowired private BCryptPasswordEncoder passwordEncoder; // Added for user creation/updates

    private User requireAdmin(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        if (u == null || !"ADMIN".equals(u.getRoleName())) return null;
        return u;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("pendingTeachers", userDAO.findPendingTeachers());
        model.addAttribute("totalCourses", courseDAO.findAll().size());
        model.addAttribute("totalSurveys", surveyDAO.findAll().size());
        return "admin/dashboard";
    }

    // ---- Teacher approvals ----
    @GetMapping("/teachers")
    public String teachers(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
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
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("courses", courseDAO.findAll());
        model.addAttribute("course", new Course());
        return "admin/courses";
    }

    @PostMapping("/courses/save")
    public String saveCourse(@ModelAttribute Course course, HttpSession session, RedirectAttributes ra) {
        User admin = requireAdmin(session);
        if (admin == null) return "redirect:/login";
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

    @GetMapping("/courses/assign/{courseId}")
    public String assignPage(@PathVariable int courseId, HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("course", courseDAO.findById(courseId));
        model.addAttribute("allTeachers", userDAO.findByRole("TEACHER"));
        return "admin/assign-teacher";
    }

    @PostMapping("/courses/assign")
    public String doAssign(@RequestParam int teacherId, @RequestParam int courseId, RedirectAttributes ra) {
        courseDAO.assignTeacher(teacherId, courseId);
        ra.addFlashAttribute("success", "Teacher assigned.");
        return "redirect:/admin/courses";
    }

    @GetMapping("/surveys")
    public String surveys(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("surveys", surveyDAO.findAll());
        return "admin/surveys";
    }

    // ---- Users Management (Add, Edit, Delete) ----

    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        if (requireAdmin(session) == null) return "redirect:/login";
        model.addAttribute("users", userDAO.findAll());

        // If we are not currently editing someone, supply a blank User object for the "Add" form
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new User());
        }
        return "admin/users";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("userForm") User user, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/login";

        if (user.getUserId() == 0) {
            // Creating a new user
            if (userDAO.existsByUsername(user.getUsername())) {
                ra.addFlashAttribute("error", "Username already taken.");
                return "redirect:/admin/users";
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDAO.save(user);
            ra.addFlashAttribute("success", "User created successfully.");
        } else {
            // Updating an existing user
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                // Admin chose to reset their password
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userDAO.updateWithPassword(user);
            } else {
                // Keep existing password
                userDAO.update(user);
            }
            ra.addFlashAttribute("success", "User details updated.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/login";
        User userToEdit = userDAO.findById(id);
        if (userToEdit != null) {
            ra.addFlashAttribute("userForm", userToEdit); // Pass the user back to the form
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable int id, HttpSession session, RedirectAttributes ra) {
        if (requireAdmin(session) == null) return "redirect:/login";
        try {
            userDAO.delete(id);
            ra.addFlashAttribute("success", "User deleted successfully.");
        } catch (Exception e) {
            // Gracefully handle database constraint errors (e.g. trying to delete a user who owns active courses)
            ra.addFlashAttribute("error", "Cannot delete this user. They are currently linked to active courses or surveys.");
        }
        return "redirect:/admin/users";
    }
}