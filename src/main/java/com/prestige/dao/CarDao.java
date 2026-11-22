package com.prestige.dao;

import com.prestige.db.DataAccessException;
import com.prestige.db.Database;
import com.prestige.db.Sql;
import com.prestige.model.Car;
import com.prestige.model.Page;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public final class CarDao {

    private static final String COLUMNS = "id, name, brand, category, price_per_day, hp, top_speed, status, image";

    private static Car map(ResultSet rs) throws SQLException {
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
        return car;
    }

    public Car find(long id) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNS + " FROM cars WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public List<Car> featured(int limit) {
        List<Car> cars = new ArrayList<>();
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("SELECT " + COLUMNS
                     + " FROM cars WHERE status = 'available' ORDER BY created_at DESC, id DESC LIMIT ?")) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cars.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
        return cars;
    }

    public long count() {
        return scalar("SELECT COUNT(*) FROM cars");
    }

    public long countAvailable() {
        return scalar("SELECT COUNT(*) FROM cars WHERE status = 'available'");
    }

    private long scalar(String sql) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0L;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public Page<Car> search(String category, String term, String sort, int page, int size) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        List<Object> args = new ArrayList<>();
        if (category != null && !category.isBlank()) {
            where.append(" AND category = ?");
            args.add(category);
        }
        if (term != null && !term.isBlank()) {
            where.append(" AND (name LIKE ? OR brand LIKE ?)");
            String like = "%" + term.trim() + "%";
            args.add(like);
            args.add(like);
        }
        String order;
        if ("price_asc".equals(sort)) {
            order = " ORDER BY price_per_day ASC, id DESC";
        } else if ("price_desc".equals(sort)) {
            order = " ORDER BY price_per_day DESC, id DESC";
        } else {
            order = " ORDER BY created_at DESC, id DESC";
        }
        int current = Math.max(1, page);
        List<Car> cars = new ArrayList<>();
        try (Connection cn = Database.connection()) {
            long total = Sql.count(cn, "SELECT COUNT(*) FROM cars" + where, args);
            List<Object> pageArgs = new ArrayList<>(args);
            pageArgs.add(size);
            pageArgs.add((long) (current - 1) * size);
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT " + COLUMNS + " FROM cars" + where + order + " LIMIT ? OFFSET ?")) {
                Sql.bind(ps, pageArgs);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        cars.add(map(rs));
                    }
                }
            }
            return new Page<>(cars, current, size, total);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public long create(Car car) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO cars (name, brand, category, price_per_day, hp, top_speed, status, image) "
                             + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, car.getName());
            ps.setString(2, car.getBrand());
            ps.setString(3, car.getCategory());
            ps.setBigDecimal(4, car.getPricePerDay());
            ps.setInt(5, car.getHp());
            ps.setInt(6, car.getTopSpeed());
            ps.setString(7, car.getStatus());
            ps.setString(8, car.getImage());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : -1L;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void update(Car car) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement(
                     "UPDATE cars SET name = ?, brand = ?, category = ?, price_per_day = ?, hp = ?, "
                             + "top_speed = ?, status = ?, image = ? WHERE id = ?")) {
            ps.setString(1, car.getName());
            ps.setString(2, car.getBrand());
            ps.setString(3, car.getCategory());
            ps.setBigDecimal(4, car.getPricePerDay());
            ps.setInt(5, car.getHp());
            ps.setInt(6, car.getTopSpeed());
            ps.setString(7, car.getStatus());
            ps.setString(8, car.getImage());
            ps.setLong(9, car.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public void delete(long id) {
        try (Connection cn = Database.connection();
             PreparedStatement ps = cn.prepareStatement("DELETE FROM cars WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
