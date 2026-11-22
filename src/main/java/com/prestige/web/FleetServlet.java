package com.prestige.web;

import com.prestige.dao.BookingDao;
import com.prestige.dao.BookingException;
import com.prestige.dao.CarDao;
import com.prestige.dao.FavoriteDao;
import com.prestige.model.Car;
import com.prestige.model.Page;
import com.prestige.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/fleet/*")
public class FleetServlet extends HttpServlet {

    private static final int PAGE_SIZE = 9;

    private final CarDao cars = new CarDao();
    private final BookingDao bookings = new BookingDao();
    private final FavoriteDao favorites = new FavoriteDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String[] segments = Web.segments(req);
        if (segments.length == 0) {
            list(req, res);
            return;
        }
        long id = Web.parseLong(segments[0], -1L);
        Car car = segments.length == 1 && id > 0 ? cars.find(id) : null;
        if (car == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        User user = Web.user(req);
        if (user != null) {
            favorites.mark(user.getId(), List.of(car));
        }
        req.setAttribute("car", car);
        Web.view(req, res, "fleet/show");
    }

    private void list(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String category = Web.text(req, "category");
        String search = Web.text(req, "search");
        String sort = Web.text(req, "sort");
        int page = Web.parseInt(req.getParameter("page"), 1);

        Page<Car> result = cars.search(category, search, sort, page, PAGE_SIZE);
        User user = Web.user(req);
        if (user != null) {
            favorites.mark(user.getId(), result.getItems());
        }

        StringBuilder query = new StringBuilder();
        appendQuery(query, "search", search);
        appendQuery(query, "category", category);
        appendQuery(query, "sort", sort);

        req.setAttribute("cars", result);
        req.setAttribute("categories", Car.CATEGORIES);
        req.setAttribute("filterSearch", search);
        req.setAttribute("filterCategory", category);
        req.setAttribute("filterSort", sort);
        req.setAttribute("baseQuery", query.toString());
        Web.view(req, res, "fleet/index");
    }

    private static void appendQuery(StringBuilder query, String key, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (query.length() > 0) {
            query.append('&');
        }
        query.append(key).append('=').append(Web.encode(value));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String[] segments = Web.segments(req);
        if (segments.length != 2 || !"book".equals(segments[1])) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        long carId = Web.parseLong(segments[0], -1L);
        User user = Web.user(req);
        if (user == null) {
            Web.redirect(req, res, "/login");
            return;
        }
        Car car = carId > 0 ? cars.find(carId) : null;
        if (car == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String backTo = "list".equals(req.getParameter("redirect_to")) ? "/fleet" : "/fleet/" + car.getId();
        List<String> errors = new ArrayList<>();
        LocalDate start = parseDate(Web.text(req, "start_date"));
        LocalDate end = parseDate(Web.text(req, "end_date"));
        if (start == null) {
            errors.add("Tanggal mulai wajib diisi dengan format yang benar.");
        } else if (start.isBefore(LocalDate.now())) {
            errors.add("Tanggal mulai tidak boleh sebelum hari ini.");
        }
        if (end == null) {
            errors.add("Tanggal selesai wajib diisi dengan format yang benar.");
        } else if (start != null && end.isBefore(start)) {
            errors.add("Tanggal selesai tidak boleh sebelum tanggal mulai.");
        }
        if (!errors.isEmpty()) {
            Web.flashErrors(req, errors);
            Web.redirect(req, res, backTo);
            return;
        }

        try {
            bookings.book(user.getId(), car.getId(), start, end);
        } catch (BookingException e) {
            Web.flashError(req, e.getMessage());
            Web.redirect(req, res, backTo);
            return;
        }
        Web.flashSuccess(req, "Booking berhasil dibuat, menunggu persetujuan admin.");
        Web.redirect(req, res, "/my-bookings");
    }

    private static LocalDate parseDate(String value) {
        try {
            return value.isEmpty() ? null : LocalDate.parse(value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
