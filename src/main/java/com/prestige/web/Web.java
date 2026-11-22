package com.prestige.web;

import com.prestige.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Web {

    public static final String AUTH_USER = "authUser";
    public static final String CSRF = "csrfToken";
    public static final String INTENDED = "intendedUrl";
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERRORS = "flashErrors";
    public static final String FLASH_OLD = "old";

    private Web() {
    }

    public static String path(HttpServletRequest req) {
        String servletPath = req.getServletPath() == null ? "" : req.getServletPath();
        String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();
        return servletPath + pathInfo;
    }

    public static String[] segments(HttpServletRequest req) {
        String info = req.getPathInfo();
        if (info == null || info.isBlank()) {
            return new String[0];
        }
        List<String> parts = new ArrayList<>();
        for (String part : info.split("/")) {
            if (!part.isEmpty()) {
                parts.add(part);
            }
        }
        return parts.toArray(new String[0]);
    }

    public static void view(HttpServletRequest req, HttpServletResponse res, String name)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + name + ".jsp").forward(req, res);
    }

    public static void redirect(HttpServletRequest req, HttpServletResponse res, String path) throws IOException {
        res.sendRedirect(req.getContextPath() + path);
    }

    public static User user(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute(AUTH_USER);
    }

    public static void flashSuccess(HttpServletRequest req, String message) {
        req.getSession(true).setAttribute(FLASH_SUCCESS, message);
    }

    public static void flashError(HttpServletRequest req, String message) {
        flashErrors(req, List.of(message));
    }

    public static void flashErrors(HttpServletRequest req, List<String> messages) {
        req.getSession(true).setAttribute(FLASH_ERRORS, new ArrayList<>(messages));
    }

    public static void flashOld(HttpServletRequest req, Map<String, String> values) {
        req.getSession(true).setAttribute(FLASH_OLD, new java.util.HashMap<>(values));
    }

    public static String text(HttpServletRequest req, String name) {
        String value = req.getParameter(name);
        return value == null ? "" : value.trim();
    }

    public static long parseLong(String value, long fallback) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException | NullPointerException e) {
            return fallback;
        }
    }

    public static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return fallback;
        }
    }

    public static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    public static String safeBack(HttpServletRequest req, String fallback) {
        String back = req.getParameter("back");
        if (back == null || back.isBlank() || !back.startsWith("/") || back.startsWith("//")
                || back.indexOf('\\') >= 0 || back.indexOf('\r') >= 0 || back.indexOf('\n') >= 0) {
            return fallback;
        }
        return back;
    }

    public static boolean isUnder(String path, String prefix) {
        return path.equals(prefix) || path.startsWith(prefix + "/");
    }
}
