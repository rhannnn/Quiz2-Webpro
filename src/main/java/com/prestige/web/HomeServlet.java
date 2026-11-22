package com.prestige.web;

import com.prestige.dao.CarDao;
import com.prestige.dao.FavoriteDao;
import com.prestige.dao.TestimonialDao;
import com.prestige.model.Car;
import com.prestige.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {""})
public class HomeServlet extends HttpServlet {

    private final CarDao cars = new CarDao();
    private final TestimonialDao testimonials = new TestimonialDao();
    private final FavoriteDao favorites = new FavoriteDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        List<Car> featured = cars.featured(3);
        User user = Web.user(req);
        if (user != null) {
            favorites.mark(user.getId(), featured);
        }
        req.setAttribute("featuredCars", featured);
        req.setAttribute("testimonials", testimonials.latest(3));
        req.setAttribute("availableCount", cars.countAvailable());
        Web.view(req, res, "home");
    }
}
