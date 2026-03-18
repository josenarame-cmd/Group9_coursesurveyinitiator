package com.courseeval.controller;

import com.courseeval.dao.UserDAO;
import com.courseeval.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class AuthController {

    @Autowired private UserDAO userDAO;
    @Autowired private BCryptPasswordEncoder passwordEncoder;

    // ---- Login ----

    @GetMapping("/login")
    public String loginPage() {
        return "common/login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username,
                          @RequestParam String password,
                          HttpSession session,
                          RedirectAttributes ra) {
        User user = userDAO.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            ra.addFlashAttribute("error", "Invalid username or password.");
            return "redirect:/login";
        }
        if ("PENDING".equals(user.getStatus())) {
            ra.addFlashAttribute("error", "Your account is pending admin approval.");
            return "redirect:/login";
        }
        if ("REJECTED".equals(user.getStatus())) {
            ra.addFlashAttribute("error", "Your account has been rejected.");
            return "redirect:/login";
        }
        session.setAttribute("loggedUser", user);

        // FIX: Respondents don't have a dashboard, send them to the survey list
        if ("RESPONDENT".equals(user.getRoleName())) {
            return "redirect:/survey/list";
        }
        return "redirect:/" + user.getRoleName().toLowerCase() + "/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ---- Teacher Registration ----

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "common/register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, RedirectAttributes ra) {
        if (userDAO.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already taken.");
            return "redirect:/register";
        }
        if (userDAO.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleId(3);           // TEACHER role id
        user.setStatus("PENDING");
        userDAO.save(user);
        ra.addFlashAttribute("success", "Registration submitted. Wait for admin approval.");
        return "redirect:/login";
    }

    // ---- Home redirect ----

    @GetMapping("/")
    public String home(HttpSession session) {
        User u = (User) session.getAttribute("loggedUser");
        if (u == null) return "redirect:/login";

        // FIX: Respondents don't have a dashboard, send them to the survey list
        if ("RESPONDENT".equals(u.getRoleName())) {
            return "redirect:/survey/list";
        }
        return "redirect:/" + u.getRoleName().toLowerCase() + "/dashboard";
    }

    // ---- Admin Registration ----

    @GetMapping("/register-admin")
    public String registerAdminPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("registerTitle", "Admin Registration");
        model.addAttribute("registerAction", "/register-admin");
        return "common/register";
    }

    @PostMapping("/register-admin")
    public String doRegisterAdmin(@ModelAttribute User user, RedirectAttributes ra) {
        if (userDAO.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already taken.");
            return "redirect:/register-admin";
        }
        if (userDAO.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register-admin";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleId(1);           // 1 is the role_id for ADMIN
        user.setStatus("ACTIVE");    // Admins bypass the 'PENDING' approval state

        userDAO.save(user);
        ra.addFlashAttribute("success", "Admin account created successfully! You can now log in.");
        return "redirect:/login";
    }

    // ---- Initiator Registration ----

    @GetMapping("/register-initiator")
    public String registerInitiatorPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("registerTitle", "Initiator Registration");
        model.addAttribute("registerAction", "/register-initiator");
        return "common/register";
    }

    @PostMapping("/register-initiator")
    public String doRegisterInitiator(@ModelAttribute User user, RedirectAttributes ra) {
        if (userDAO.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already taken.");
            return "redirect:/register-initiator";
        }
        if (userDAO.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register-initiator";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleId(2);           // 2 is the role_id for INITIATOR
        user.setStatus("ACTIVE");    // Initiators are active immediately

        userDAO.save(user);
        ra.addFlashAttribute("success", "Initiator account created successfully! You can now log in.");
        return "redirect:/login";
    }

    // ---- Student / Respondent Registration ----

    @GetMapping("/register-student")
    public String registerStudentPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("registerTitle", "Student Registration");
        model.addAttribute("registerAction", "/register-student");
        return "common/register";
    }

    @PostMapping("/register-student")
    public String doRegisterStudent(@ModelAttribute User user, RedirectAttributes ra) {
        if (userDAO.existsByUsername(user.getUsername())) {
            ra.addFlashAttribute("error", "Username already taken.");
            return "redirect:/register-student";
        }
        if (userDAO.existsByEmail(user.getEmail())) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register-student";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleId(4);           // 4 is the role_id for RESPONDENT
        user.setStatus("ACTIVE");    // Students don't need admin approval

        userDAO.save(user);
        ra.addFlashAttribute("success", "Student account created! You can now log in.");
        return "redirect:/login";
    }
}