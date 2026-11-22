package com.prestige.web;

import com.prestige.dao.BookingDao;
import com.prestige.model.Booking;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/bookings/*")
public class AdminBookingServlet extends HttpServlet {

    private final BookingDao bookings = new BookingDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (Web.segments(req).length != 0) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String status = Web.text(req, "status");
        if (!Booking.STATUSES.contains(status)) {
            status = "";
        }
        int page = Web.parseInt(req.getParameter("page"), 1);
        req.setAttribute("bookings", bookings.all(status, page, 10));
        req.setAttribute("statuses", Booking.STATUSES);
        req.setAttribute("filterStatus", status);
        req.setAttribute("baseQuery", status.isEmpty() ? "" : "status=" + Web.encode(status));
        Web.view(req, res, "admin/bookings/index");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String[] segments = Web.segments(req);
        if (segments.length != 2 || !"status".equals(segments[1])) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String status = Web.text(req, "status");
        if (!Booking.STATUSES.contains(status)) {
            Web.flashError(req, "Status tidak valid.");
            Web.redirect(req, res, Web.safeBack(req, "/admin/bookings"));
            return;
        }
        if (!bookings.adminSetStatus(Web.parseLong(segments[0], -1L), status)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Web.flashSuccess(req, "Status booking berhasil diperbarui.");
        Web.redirect(req, res, Web.safeBack(req, "/admin/bookings"));
    }
}
