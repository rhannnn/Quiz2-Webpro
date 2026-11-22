package com.prestige.dao;

import com.prestige.db.DataAccessException;
import com.prestige.db.Database;
import com.prestige.db.Sql;
import com.prestige.model.Booking;
import com.prestige.model.Page;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public final class BookingDao {

    private static final String SELECT = "SELECT b.id, b.user_id, b.car_id, b.start_date, b.end_date, "
            + "b.total_price, b.status, u.name AS user_name, u.email AS user_email, c.name AS car_name, "
            + "c.brand AS car_brand, c.image AS car_image, c.price_per_day AS car_price "
            + "FROM bookings b JOIN users u ON u.id = b.user_id JOIN cars c ON c.id = b.car_id";

    private static Booking map(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getLong("id"));
        booking.setUserId(rs.getLong("user_id"));
        booking.setCarId(rs.getLong("car_id"));
        booking.setStartDate(rs.getObject("start_date", LocalDate.class));
        booking.setEndDate(rs.getObject("end_date", LocalDate.class));
        booking.setTotalPrice(rs.getBigDecimal("total_price"));
        booking.setStatus(rs.getString("status"));
        booking.setUserName(rs.getString("user_name"));
        booking.setUserEmail(rs.getString("user_email"));
        booking.setCarName(rs.getString("car_name"));
        booking.setCarBrand(rs.getString("car_brand"));
        booking.setCarImage(rs.getString("car_image"));
        booking.setCarPricePerDay(rs.getBigDecimal("car_price"));
        return booking;
    }

    public Booking find(long id) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " WHERE b.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public Page<Booking> byUser(long userId, int page, int size) {
        return paged(" WHERE b.user_id = ?", List.of(userId), page, size);
    }

    public Page<Booking> all(String status, int page, int size) {
        if (status != null && !status.isBlank()) {
            return paged(" WHERE b.status = ?", List.of(status), page, size);
        }
        return paged("", List.of(), page, size);
    }

    private Page<Booking> paged(String where, List<Object> args, int page, int size) {
        int current = Math.max(1, page);
        List<Booking> items = new ArrayList<>();
        try (Connection cn = Database.connection()) {
            long total = Sql.count(cn, "SELECT COUNT(*) FROM bookings b" + where, args);
            List<Object> pageArgs = new ArrayList<>(args);
            pageArgs.add(size);
            pageArgs.add((long) (current - 1) * size);
            try (PreparedStatement ps = cn.prepareStatement(
                    SELECT + where + " ORDER BY b.created_at DESC, b.id DESC LIMIT ? OFFSET ?")) {
                Sql.bind(ps, pageArgs);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(map(rs));
                    }
                }
            }
            return new Page<>(items, current, size, total);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public List<Booking> latest(int limit) {
        List<Booking> items = new ArrayList<>();
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(SELECT + " ORDER BY b.created_at DESC, b.id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return items;
    }

    public long countPending() {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM bookings WHERE status = 'pending'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public BigDecimal revenue() {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT COALESCE(SUM(total_price), 0) FROM bookings WHERE status IN ('approved', 'completed')");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public long book(long userId, long carId, LocalDate start, LocalDate end) throws BookingException {
        try (Connection cn = Database.connection()) {
            cn.setAutoCommit(false);
            try {
                String status;
                BigDecimal price;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT status, price_per_day FROM cars WHERE id = ? FOR UPDATE")) {
                    ps.setLong(1, carId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new BookingException("Kendaraan tidak ditemukan.");
                        }
                        status = rs.getString(1);
                        price = rs.getBigDecimal(2);
                    }
                }
                if (!"available".equals(status)) {
                    throw new BookingException("Kendaraan sedang tidak tersedia.");
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT 1 FROM bookings WHERE car_id = ? AND status IN ('pending', 'approved') "
                                + "AND start_date <= ? AND end_date >= ? LIMIT 1")) {
                    ps.setLong(1, carId);
                    ps.setObject(2, end);
                    ps.setObject(3, start);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            throw new BookingException("Kendaraan sudah dipesan pada rentang tanggal tersebut.");
                        }
                    }
                }
                long days = ChronoUnit.DAYS.between(start, end) + 1;
                BigDecimal total = price.multiply(BigDecimal.valueOf(days));
                long id;
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO bookings (user_id, car_id, start_date, end_date, total_price, status) "
                                + "VALUES (?, ?, ?, ?, ?, 'pending')", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, userId);
                    ps.setLong(2, carId);
                    ps.setObject(3, start);
                    ps.setObject(4, end);
                    ps.setBigDecimal(5, total);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        id = keys.next() ? keys.getLong(1) : -1L;
                    }
                }
                cn.commit();
                return id;
            } catch (BookingException | SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void cancel(long id) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("UPDATE bookings SET status = 'cancelled' WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean adminSetStatus(long id, String status) {
        try (Connection cn = Database.connection()) {
            cn.setAutoCommit(false);
            try {
                long carId;
                try (PreparedStatement ps = cn.prepareStatement("SELECT car_id FROM bookings WHERE id = ? FOR UPDATE")) {
                    ps.setLong(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            cn.rollback();
                            return false;
                        }
                        carId = rs.getLong(1);
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement("UPDATE bookings SET status = ? WHERE id = ?")) {
                    ps.setString(1, status);
                    ps.setLong(2, id);
                    ps.executeUpdate();
                }
                String carStatus = null;
                if ("approved".equals(status)) {
                    carStatus = "rented";
                } else if ("completed".equals(status) || "cancelled".equals(status)) {
                    carStatus = "available";
                }
                if (carStatus != null) {
                    try (PreparedStatement ps = cn.prepareStatement("UPDATE cars SET status = ? WHERE id = ?")) {
                        ps.setString(1, carStatus);
                        ps.setLong(2, carId);
                        ps.executeUpdate();
                    }
                }
                cn.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
