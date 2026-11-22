package com.prestige.web;

import com.prestige.dao.TestimonialDao;
import com.prestige.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/testimonials")
public class TestimonialServlet extends HttpServlet {

    private final TestimonialDao testimonials = new TestimonialDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("testimonials", testimonials.page(Web.parseInt(req.getParameter("page"), 1), 9));
        Web.view(req, res, "testimonials/index");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = Web.user(req);
        if (user == null) {
            Web.redirect(req, res, "/login");
            return;
        }
        String comment = Web.text(req, "comment");
        int rating = Web.parseInt(req.getParameter("rating"), 0);

        List<String> errors = new ArrayList<>();
        if (comment.isEmpty() || comment.length() > 500) {
            errors.add("Komentar wajib diisi (maksimal 500 karakter).");
        }
        if (rating < 1 || rating > 5) {
            errors.add("Rating harus antara 1 sampai 5.");
        }
        if (!errors.isEmpty()) {
            Web.flashErrors(req, errors);
            Web.flashOld(req, Map.of("comment", comment));
            Web.redirect(req, res, "/testimonials");
            return;
        }
        testimonials.create(user.getName(), comment, rating);
        Web.flashSuccess(req, "Terima kasih atas testimoni Anda.");
        Web.redirect(req, res, "/testimonials");
    }
}
