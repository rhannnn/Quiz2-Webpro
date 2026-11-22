package com.prestige.web;

import com.prestige.config.AppConfig;
import com.prestige.db.Database;
import com.prestige.db.SchemaInit;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.nio.file.Files;

@WebListener
public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            try {
                Files.createDirectories(AppConfig.uploadDir().resolve("cars"));
            } catch (IOException e) {
                throw new IllegalStateException("Folder upload tidak bisa dibuat: " + AppConfig.uploadDir(), e);
            }
            String dbUrl = AppConfig.dbUrl();
            String safeDbUrl = dbUrl.replaceAll("://[^/@]*@", "://***:***@");
            System.out.println("[prestige] Starting with DB: " + safeDbUrl);
            Database.init();
            SchemaInit.run();
            SchemaInit.restoreSeedImages();
            System.out.println("[prestige] Startup complete.");
        } catch (Throwable t) {
            System.err.println("[prestige] Startup failed:");
            t.printStackTrace(System.err);
            throw t instanceof RuntimeException re ? re : new RuntimeException(t);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Database.close();
    }
}
