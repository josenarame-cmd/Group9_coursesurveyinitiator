package com.courseeval.util;

import com.courseeval.model.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedUser");

        // Public paths
        if (path.equals("/") || path.equals("/login") || path.equals("/register") || 
            path.startsWith("/survey/") || path.startsWith("/css/") || path.startsWith("/js/")) {
            return true;
        }

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        // Role-based access
        if (path.startsWith("/admin/") && !"ADMIN".equals(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin role required");
            return false;
        }
        if (path.startsWith("/initiator/") && !"INITIATOR".equals(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Initiator role required");
            return false;
        }
        if (path.startsWith("/teacher/") && !"TEACHER".equals(user.getRoleName())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Teacher role required");
            return false;
        }

        return true;
    }
}
