package com.prestige.dao;

import com.prestige.db.DataAccessException;
import com.prestige.db.Database;
import com.prestige.db.Sql;
import com.prestige.model.Page;
import com.prestige.model.Testimonial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class TestimonialDao {

    private static Testimonial map(ResultSet rs) throws SQLException {
        Testimonial t = new Testimonial();
        t.setId(rs.getLong("id"));
        t.setUserName(rs.getString("user_name"));
        t.setComment(rs.getString("comment"));
        t.setRating(rs.getInt("rating"));
        return t;
    }

    public List<Testimonial> latest(int limit) {
        List<Testimonial> items = new ArrayList<>();
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT id, user_name, comment, rating FROM testimonials ORDER BY created_at DESC, id DESC LIMIT ?")) {
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

    public Page<Testimonial> page(int page, int size) {
        int current = Math.max(1, page);
        List<Testimonial> items = new ArrayList<>();
        try (Connection cn = Database.connection()) {
            long total = Sql.count(cn, "SELECT COUNT(*) FROM testimonials", List.of());
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT id, user_name, comment, rating FROM testimonials "
                            + "ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?")) {
                ps.setInt(1, size);
                ps.setLong(2, (long) (current - 1) * size);
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

    public void create(String userName, String comment, int rating) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO testimonials (user_name, comment, rating) VALUES (?, ?, ?)")) {
            ps.setString(1, userName);
            ps.setString(2, comment);
            ps.setInt(3, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
