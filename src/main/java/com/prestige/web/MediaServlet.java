package com.prestige.web;

import com.prestige.config.AppConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

@WebServlet("/media/*")
public class MediaServlet extends HttpServlet {

    private static final Pattern SAFE = Pattern.compile("^/cars/[A-Za-z0-9._-]+\\.(jpg|jpeg|png|webp)$");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String info = req.getPathInfo();
        if (info == null || !SAFE.matcher(info).matches() || info.contains("..")) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Path root = AppConfig.uploadDir();
        Path file = root.resolve(info.substring(1)).normalize();
        if (!file.startsWith(root) || !Files.isRegularFile(file)) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String name = file.getFileName().toString().toLowerCase(Locale.ROOT);
        String type = name.endsWith(".png") ? "image/png" : name.endsWith(".webp") ? "image/webp" : "image/jpeg";
        res.setContentType(type);
        res.setHeader("Cache-Control", "public, max-age=86400");
        res.setHeader("X-Content-Type-Options", "nosniff");
        res.setContentLengthLong(Files.size(file));
        try (OutputStream out = res.getOutputStream()) {
            Files.copy(file, out);
        }
    }
}
