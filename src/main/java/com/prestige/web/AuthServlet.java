package com.prestige.web;

import com.prestige.dao.UserDao;
import com.prestige.model.User;
import com.prestige.util.Passwords;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserDao users = new UserDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/logout".equals(path)) {
            Web.redirect(req, res, "/");
            return;
        }
        if (Web.user(req) != null) {
            Web.redirect(req, res, "/");
            return;
        }
        Web.view(req, res, "/login".equals(path) ? "auth/login" : "auth/register");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/login" -> login(req, res);
            case "/register" -> register(req, res);
            default -> logout(req, res);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String email = Web.text(req, "email");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");

        List<String> errors = new ArrayList<>();
        if (email.isEmpty() || !EMAIL.matcher(email).matches()) {
            errors.add("Email wajib diisi dengan format yang benar.");
        }
        if (password.isEmpty()) {
            errors.add("Password wajib diisi.");
        }
        User found = errors.isEmpty() ? users.findByEmail(email) : null;
        if (errors.isEmpty() && (found == null || !Passwords.verify(password, found.getPasswordHash()))) {
            errors.add("Email atau password salah.");
        }
        if (!errors.isEmpty()) {
            Web.flashErrors(req, errors);
            Web.flashOld(req, Map.of("email", email));
            Web.redirect(req, res, "/login");
            return;
        }

        HttpSession previous = req.getSession(false);
        String intended = previous == null ? null : (String) previous.getAttribute(Web.INTENDED);
        req.changeSessionId();
        HttpSession session = req.getSession(true);
        session.removeAttribute(Web.INTENDED);
        session.setAttribute(Web.AUTH_USER, found.sessionCopy());

        if (found.isAdmin()) {
            Web.redirect(req, res, "/admin/dashboard");
        } else if (intended != null && intended.startsWith(req.getContextPath() + "/")) {
            res.sendRedirect(intended);
        } else {
            Web.redirect(req, res, "/");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = Web.text(req, "name");
        String email = Web.text(req, "email");
        String password = req.getParameter("password") == null ? "" : req.getParameter("password");
        String confirmation = req.getParameter("password_confirmation");

        List<String> errors = new ArrayList<>();
        if (name.isEmpty() || name.length() > 255) {
            errors.add("Nama wajib diisi (maksimal 255 karakter).");
        }
        if (email.isEmpty() || email.length() > 255 || !EMAIL.matcher(email).matches()) {
            errors.add("Email wajib diisi dengan format yang benar.");
        } else if (users.emailExists(email)) {
            errors.add("Email sudah terdaftar.");
        }
        if (password.length() < 8) {
            errors.add("Password minimal 8 karakter.");
        } else if (!password.equals(confirmation)) {
            errors.add("Konfirmasi password tidak sama.");
        }
        if (!errors.isEmpty()) {
            Web.flashErrors(req, errors);
            Web.flashOld(req, Map.of("name", name, "email", email));
            Web.redirect(req, res, "/register");
            return;
        }

        long id = users.create(name, email, Passwords.hash(password), "customer");
        if (id < 0) {
            Web.flashError(req, "Email sudah terdaftar.");
            Web.flashOld(req, Map.of("name", name, "email", email));
            Web.redirect(req, res, "/register");
            return;
        }

        User created = new User();
        created.setId(id);
        created.setName(name);
        created.setEmail(email);
        created.setRole("customer");

        req.changeSessionId();
        req.getSession(true).setAttribute(Web.AUTH_USER, created);
        Web.redirect(req, res, "/");
    }

    private void logout(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Web.redirect(req, res, "/");
    }
}
