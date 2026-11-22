package com.prestige.web;

import com.prestige.model.User;
import com.prestige.util.Passwords;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AppFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");
        res.setCharacterEncoding("UTF-8");

        String path = Web.path(req);
        if (path.startsWith("/assets/") || path.startsWith("/media/")) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(Web.CSRF);
        if (token == null) {
            token = Passwords.newToken();
            session.setAttribute(Web.CSRF, token);
        }
        User user = (User) session.getAttribute(Web.AUTH_USER);

        String query = req.getQueryString();
        req.setAttribute("base", req.getContextPath());
        req.setAttribute("csrf", token);
        req.setAttribute("authUser", user);
        req.setAttribute("currentPath", path);
        req.setAttribute("currentUrl", path + (query == null ? "" : "?" + query));
        moveFlash(session, req);

        if ("POST".equalsIgnoreCase(req.getMethod()) && !validCsrf(req, token)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Token keamanan tidak valid atau sesi berakhir. Muat ulang halaman lalu coba lagi.");
            return;
        }

        boolean admin = Web.isUnder(path, "/admin");
        boolean guarded = admin || Web.isUnder(path, "/my-bookings") || Web.isUnder(path, "/favorites");
        if (guarded) {
            if (user == null) {
                if ("GET".equalsIgnoreCase(req.getMethod())) {
                    session.setAttribute(Web.INTENDED, req.getContextPath() + req.getAttribute("currentUrl"));
                }
                res.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            if (admin && !user.isAdmin()) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private static void moveFlash(HttpSession session, HttpServletRequest req) {
        String[] keys = {Web.FLASH_SUCCESS, Web.FLASH_ERRORS, Web.FLASH_OLD};
        for (String key : keys) {
            Object value = session.getAttribute(key);
            if (value != null) {
                req.setAttribute(key, value);
                session.removeAttribute(key);
            }
        }
    }

    private static boolean validCsrf(HttpServletRequest req, String token) {
        String given = req.getHeader("X-CSRF-Token");
        if (given == null) {
            given = req.getParameter("_csrf");
        }
        return Passwords.safeEquals(given, token);
    }
}
