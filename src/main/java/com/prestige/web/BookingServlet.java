package com.prestige.web;

import com.prestige.dao.BookingDao;
import com.prestige.model.Booking;
import com.prestige.model.Page;
import com.prestige.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/my-bookings/*")
public class BookingServlet extends HttpServlet {

    private final BookingDao bookings = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = Web.user(req);
        String[] segments = Web.segments(req);
        if (segments.length == 0) {
            Page<Booking> page = bookings.byUser(user.getId(), Web.parseInt(req.getParameter("page"), 1), 10);
            req.setAttribute("bookings", page);
            Web.view(req, res, "bookings/index");
            return;
        }
        if (segments.length == 2 && "invoice".equals(segments[1])) {
            Booking booking = bookings.find(Web.parseLong(segments[0], -1L));
            if (booking == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (booking.getUserId() != user.getId() && !user.isAdmin()) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN, "Anda tidak berhak melihat invoice ini.");
                return;
            }
            req.setAttribute("booking", booking);
            Web.view(req, res, "bookings/invoice");
            return;
        }
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        User user = Web.user(req);
        String[] segments = Web.segments(req);
        if (segments.length != 2 || !"cancel".equals(segments[1])) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Booking booking = bookings.find(Web.parseLong(segments[0], -1L));
        if (booking == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (booking.getUserId() != user.getId() || !booking.isPending()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        bookings.cancel(booking.getId());
        Web.flashSuccess(req, "Booking dibatalkan.");
        Web.redirect(req, res, "/my-bookings");
    }
}
