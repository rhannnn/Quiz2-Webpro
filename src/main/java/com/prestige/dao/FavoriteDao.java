package com.prestige.dao;

import com.prestige.db.DataAccessException;
import com.prestige.db.Database;
import com.prestige.model.Car;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FavoriteDao {

    public boolean toggle(long userId, long carId) {
        try (Connection cn = Database.connection()) {
            try (PreparedStatement ps = cn.prepareStatement(
                    "DELETE FROM favorites WHERE user_id = ? AND car_id = ?")) {
                ps.setLong(1, userId);
                ps.setLong(2, carId);
                if (ps.executeUpdate() > 0) {
                    return false;
                }
            }
            try (PreparedStatement ps = cn.prepareStatement(
                    "INSERT IGNORE INTO favorites (user_id, car_id) VALUES (?, ?)")) {
                ps.setLong(1, userId);
                ps.setLong(2, carId);
                ps.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public Set<Long> carIds(long userId) {
        Set<Long> ids = new HashSet<>();
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("SELECT car_id FROM favorites WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return ids;
    }

    public void mark(long userId, List<Car> cars) {
        if (cars.isEmpty()) {
            return;
        }
        Set<Long> ids = carIds(userId);
        for (Car car : cars) {
            car.setFavorite(ids.contains(car.getId()));
        }
    }

    public List<Car> cars(long userId) {
        List<Car> cars = new ArrayList<>();
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT c.id, c.name, c.brand, c.category, c.price_per_day, c.hp, c.top_speed, c.status, c.image "
                             + "FROM favorites f JOIN cars c ON c.id = f.car_id WHERE f.user_id = ? "
                             + "ORDER BY f.created_at DESC, c.id DESC")) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Car car = new Car();
                    car.setId(rs.getLong("id"));
                    car.setName(rs.getString("name"));
                    car.setBrand(rs.getString("brand"));
                    car.setCategory(rs.getString("category"));
                    car.setPricePerDay(rs.getBigDecimal("price_per_day"));
                    car.setHp(rs.getInt("hp"));
                    car.setTopSpeed(rs.getInt("top_speed"));
                    car.setStatus(rs.getString("status"));
                    car.setImage(rs.getString("image"));
                    car.setFavorite(true);
                    cars.add(car);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return cars;
    }
}
