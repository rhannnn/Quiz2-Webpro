package com.prestige.web;

import com.prestige.dao.CarDao;
import com.prestige.dao.FavoriteDao;
import com.prestige.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/favorites/*")
public class FavoriteServlet extends HttpServlet {

    private final FavoriteDao favorites = new FavoriteDao();
    private final CarDao cars = new CarDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (Web.segments(req).length != 0) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User user = Web.user(req);
        req.setAttribute("favoriteCars", favorites.cars(user.getId()));
        Web.view(req, res, "favorites/index");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String[] segments = Web.segments(req);
        if (segments.length != 2 || !"toggle".equals(segments[1])) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        long carId = Web.parseLong(segments[0], -1L);
        if (carId < 1 || cars.find(carId) == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User user = Web.user(req);
        boolean added = favorites.toggle(user.getId(), carId);
        Web.flashSuccess(req, added ? "Ditambahkan ke favorit." : "Dihapus dari favorit.");
        Web.redirect(req, res, Web.safeBack(req, "/favorites"));
    }
}
