package com.prestige.dao;

import com.prestige.db.DataAccessException;
import com.prestige.db.Database;
import com.prestige.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

public final class UserDao {

    private static final String COLUMNS = "id, name, email, password, role";

    private static User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }

    public User findByEmail(String email) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNS + " FROM users WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    public long create(String name, String email, String passwordHash, String role) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : -1L;
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            return -1L;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public long countCustomers() {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("SELECT COUNT(*) FROM users WHERE role = 'customer'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
