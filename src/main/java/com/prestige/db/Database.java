package com.prestige.db;

import com.prestige.config.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {

    private static HikariDataSource dataSource;

    private Database() {
    }

    public static synchronized void init() {
        if (dataSource != null) {
            return;
        }
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(AppConfig.dbUrl());
        config.setUsername(AppConfig.dbUser());
        config.setPassword(AppConfig.dbPassword());
        config.setMaximumPoolSize(AppConfig.dbPoolSize());
        config.setMinimumIdle(1);
        config.setConnectionTimeout(15000);
        config.setPoolName("prestige-pool");
        dataSource = new HikariDataSource(config);
    }

    public static Connection connection() throws SQLException {
        if (dataSource == null) {
            init();
        }
        return dataSource.getConnection();
    }

    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
