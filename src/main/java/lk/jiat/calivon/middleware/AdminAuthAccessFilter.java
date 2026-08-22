package lk.jiat.calivon.middleware;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class AdminAuthAccessFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession(false);

        boolean isUser = session != null && session.getAttribute("user") != null;
        boolean isAdmin = session != null && session.getAttribute("admin") != null;

        if (isAdmin) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (isUser) {
            response.sendRedirect("login.html"); // Admins go to admin dashboard
        } else {
            response.sendRedirect("login.html"); // No session
        }
    }
}
