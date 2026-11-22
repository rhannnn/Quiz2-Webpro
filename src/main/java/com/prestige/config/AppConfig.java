package com.prestige.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppConfig {

    private AppConfig() {
    }

    public static String get(String key, String fallback) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    public static int getInt(String key, int fallback) {
        try {
            return Integer.parseInt(get(key, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static String dbUrl() {
        String full = get("DB_URL", null);
        if (full != null) {
            return full;
        }
        return "jdbc:mysql://" + get("DB_HOST", "localhost") + ":" + get("DB_PORT", "3306") + "/"
                + get("DB_NAME", "luxury_rent_car_jsp")
                + "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8"
                + "&serverTimezone=Asia/Jakarta&useSSL=false&allowPublicKeyRetrieval=true";
    }

    public static String dbUser() {
        return get("DB_USER", "root");
    }

    public static String dbPassword() {
        return get("DB_PASSWORD", "");
    }

    public static int dbPoolSize() {
        return getInt("DB_POOL", 5);
    }

    public static Path uploadDir() {
        String configured = get("UPLOAD_DIR", null);
        if (configured != null) {
            return Paths.get(configured).toAbsolutePath().normalize();
        }
        return Paths.get(System.getProperty("user.home"), "prestige-uploads").toAbsolutePath().normalize();
    }

    public static String adminEmail() {
        return get("ADMIN_EMAIL", "admin@prestige.test");
    }

    public static String adminPassword() {
        return get("ADMIN_PASSWORD", "admin12345");
    }

    public static String customerEmail() {
        return get("CUSTOMER_EMAIL", "customer@prestige.test");
    }

    public static String customerPassword() {
        return get("CUSTOMER_PASSWORD", "customer12345");
    }
}
