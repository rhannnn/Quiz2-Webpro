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
            Files.createDirectories(AppConfig.uploadDir().resolve("cars"));
        } catch (IOException e) {
            throw new IllegalStateException("Folder upload tidak bisa dibuat: " + AppConfig.uploadDir(), e);
        }
        Database.init();
        SchemaInit.run();
        SchemaInit.restoreSeedImages();
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Database.close();
    }
}
