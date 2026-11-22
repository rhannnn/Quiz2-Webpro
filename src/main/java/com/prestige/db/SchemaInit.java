package com.prestige.db;

import com.prestige.config.AppConfig;
import com.prestige.util.Passwords;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SchemaInit {

    private static final String[] TABLES = {
        "CREATE TABLE IF NOT EXISTS users ("
            + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "name VARCHAR(255) NOT NULL,"
            + "email VARCHAR(255) NOT NULL,"
            + "password VARCHAR(255) NOT NULL,"
            + "role ENUM('admin','customer') NOT NULL DEFAULT 'customer',"
            + "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
            + "UNIQUE KEY uq_users_email (email)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS cars ("
            + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "name VARCHAR(255) NOT NULL,"
            + "brand VARCHAR(255) NOT NULL,"
            + "category ENUM('Supercar','Luxury Sedan','Motorsport','SUV') NOT NULL,"
            + "price_per_day DECIMAL(12,2) NOT NULL,"
            + "hp INT UNSIGNED NOT NULL,"
            + "top_speed INT UNSIGNED NOT NULL,"
            + "status ENUM('available','rented') NOT NULL DEFAULT 'available',"
            + "image VARCHAR(255) NULL,"
            + "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS bookings ("
            + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "user_id BIGINT UNSIGNED NOT NULL,"
            + "car_id BIGINT UNSIGNED NOT NULL,"
            + "start_date DATE NOT NULL,"
            + "end_date DATE NOT NULL,"
            + "total_price DECIMAL(14,2) NOT NULL,"
            + "status ENUM('pending','approved','completed','cancelled') NOT NULL DEFAULT 'pending',"
            + "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
            + "CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "CONSTRAINT fk_bookings_car FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS testimonials ("
            + "id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,"
            + "user_name VARCHAR(255) NOT NULL,"
            + "comment TEXT NOT NULL,"
            + "rating TINYINT UNSIGNED NOT NULL,"
            + "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",
        "CREATE TABLE IF NOT EXISTS favorites ("
            + "user_id BIGINT UNSIGNED NOT NULL,"
            + "car_id BIGINT UNSIGNED NOT NULL,"
            + "created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,"
            + "PRIMARY KEY (user_id, car_id),"
            + "CONSTRAINT fk_favorites_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
            + "CONSTRAINT fk_favorites_car FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE"
            + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
    };

    private SchemaInit() {
    }

    public static void run() {
        try (Connection connection = Database.connection()) {
            try (Statement statement = connection.createStatement()) {
                for (String ddl : TABLES) {
                    statement.execute(ddl);
                }
            }
            if (isEmpty(connection, "users")) {
                seed(connection);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private static boolean isEmpty(Connection connection, String table) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
            return rs.next() && rs.getLong(1) == 0L;
        }
    }

    private static void seed(Connection connection) throws SQLException {
        insertUser(connection, "Administrator", AppConfig.adminEmail(), AppConfig.adminPassword(), "admin");
        insertUser(connection, "Customer Demo", AppConfig.customerEmail(), AppConfig.customerPassword(), "customer");

        if (isEmpty(connection, "cars")) {
            insertCar(connection, "Revuelto V12 Hybrid", "Lamborghini", "Supercar", 45000000, 1015, 350, "revuelto.jpg");
            insertCar(connection, "750S Spider", "McLaren", "Supercar", 36000000, 740, 332, "750s.webp");
            insertCar(connection, "Ghost Black Badge", "Rolls-Royce", "Luxury Sedan", 38000000, 592, 250, "ghost.jpg");
            insertCar(connection, "DBS", "Aston Martin", "Luxury Sedan", 32000000, 715, 340, "dbs.webp");
            insertCar(connection, "296 GTB", "Ferrari", "Motorsport", 42000000, 830, 330, "296gtb.webp");
            insertCar(connection, "Urus Performante", "Lamborghini", "SUV", 28000000, 657, 306, "urus.webp");
        }

        if (isEmpty(connection, "testimonials")) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO testimonials (user_name, comment, rating) VALUES (?, ?, ?)")) {
                ps.setString(1, "H. Hartono W.");
                ps.setString(2, "Pelayanan eksklusif, mobil prima dan pengantaran tepat waktu.");
                ps.setInt(3, 5);
                ps.executeUpdate();
            }
        }
    }

    private static void insertUser(Connection connection, String name, String email, String password, String role)
            throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, Passwords.hash(password));
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    private static void insertCar(Connection connection, String name, String brand, String category, long price,
                                  int hp, int topSpeed, String seedImage) throws SQLException {
        String stored = copySeedImage(seedImage);
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO cars (name, brand, category, price_per_day, hp, top_speed, status, image) "
                        + "VALUES (?, ?, ?, ?, ?, ?, 'available', ?)")) {
            ps.setString(1, name);
            ps.setString(2, brand);
            ps.setString(3, category);
            ps.setLong(4, price);
            ps.setInt(5, hp);
            ps.setInt(6, topSpeed);
            ps.setString(7, stored);
            ps.executeUpdate();
        }
    }

    private static final String[] SEED_IMAGES = {
        "revuelto.jpg", "750s.webp", "ghost.jpg", "dbs.webp", "296gtb.webp", "urus.webp"
    };

    public static void restoreSeedImages() {
        for (String fileName : SEED_IMAGES) {
            Path target = AppConfig.uploadDir().resolve("cars").resolve(fileName);
            if (!Files.exists(target)) {
                copySeedImage(fileName);
            }
        }
    }

    private static String copySeedImage(String fileName) {
        Path directory = AppConfig.uploadDir().resolve("cars");
        Path target = directory.resolve(fileName);
        try (InputStream in = SchemaInit.class.getResourceAsStream("/seed/" + fileName)) {
            if (in == null) {
                return null;
            }
            Files.createDirectories(directory);
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            return "cars/" + fileName;
        } catch (IOException e) {
            return null;
        }
    }
}
