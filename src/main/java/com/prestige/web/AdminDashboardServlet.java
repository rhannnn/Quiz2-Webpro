package com.prestige.web;

import com.prestige.dao.BookingDao;
import com.prestige.dao.CarDao;
import com.prestige.dao.UserDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet({"/admin", "/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final CarDao cars = new CarDao();
    private final UserDao users = new UserDao();
    private final BookingDao bookings = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if ("/admin".equals(req.getServletPath())) {
            Web.redirect(req, res, "/admin/dashboard");
            return;
        }
        req.setAttribute("totalCars", cars.count());
        req.setAttribute("availableCars", cars.countAvailable());
        req.setAttribute("totalCustomers", users.countCustomers());
        req.setAttribute("pendingBookings", bookings.countPending());
        req.setAttribute("revenue", bookings.revenue());
        req.setAttribute("latestBookings", bookings.latest(5));
        Web.view(req, res, "admin/dashboard");
    }
}
