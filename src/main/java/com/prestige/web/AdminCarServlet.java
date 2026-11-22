package com.prestige.web;

import com.prestige.config.AppConfig;
import com.prestige.dao.CarDao;
import com.prestige.model.Car;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@WebServlet("/admin/cars/*")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 2L * 1024 * 1024, maxRequestSize = 3L * 1024 * 1024)
public class AdminCarServlet extends HttpServlet {

    private static final Pattern STORED_IMAGE = Pattern.compile("^cars/[A-Za-z0-9._-]+$");

    private final CarDao cars = new CarDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String[] segments = Web.segments(req);
        if (segments.length == 0) {
            req.setAttribute("cars", cars.search(null, null, null, Web.parseInt(req.getParameter("page"), 1), 10));
            req.setAttribute("baseQuery", "");
            Web.view(req, res, "admin/cars/index");
            return;
        }
        if (segments.length == 1 && "new".equals(segments[0])) {
            req.setAttribute("categories", Car.CATEGORIES);
            req.setAttribute("statuses", Car.STATUSES);
            Web.view(req, res, "admin/cars/form");
            return;
        }
        Car car = segments.length <= 2 ? cars.find(Web.parseLong(segments[0], -1L)) : null;
        if (car == null) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        req.setAttribute("car", car);
        if (segments.length == 1) {
            Web.view(req, res, "admin/cars/show");
        } else if ("edit".equals(segments[1])) {
            req.setAttribute("categories", Car.CATEGORIES);
            req.setAttribute("statuses", Car.STATUSES);
            Web.view(req, res, "admin/cars/form");
        } else {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String[] segments = Web.segments(req);
        if (segments.length == 0) {
            save(req, res, null);
            return;
        }
        long id = Web.parseLong(segments[0], -1L);
        Car car = id > 0 ? cars.find(id) : null;
        if (car == null || segments.length > 2) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (segments.length == 1) {
            save(req, res, car);
        } else if ("delete".equals(segments[1])) {
            deleteImage(car.getImage());
            cars.delete(car.getId());
            Web.flashSuccess(req, "Kendaraan dan file gambar berhasil dihapus.");
            Web.redirect(req, res, "/admin/cars");
        } else {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void save(HttpServletRequest req, HttpServletResponse res, Car existing) throws ServletException, IOException {
        boolean creating = existing == null;
        String formPath = creating ? "/admin/cars/new" : "/admin/cars/" + existing.getId() + "/edit";

        Map<String, String> old = new HashMap<>();
        for (String field : new String[]{"name", "brand", "category", "status", "hp", "top_speed", "price_per_day"}) {
            old.put(field, Web.text(req, field));
        }

        List<String> errors = new ArrayList<>();
        if (old.get("name").isEmpty() || old.get("name").length() > 255) {
            errors.add("Nama kendaraan wajib diisi (maksimal 255 karakter).");
        }
        if (old.get("brand").isEmpty() || old.get("brand").length() > 255) {
            errors.add("Brand wajib diisi (maksimal 255 karakter).");
        }
        if (!Car.CATEGORIES.contains(old.get("category"))) {
            errors.add("Kategori tidak valid.");
        }
        if (!Car.STATUSES.contains(old.get("status"))) {
            errors.add("Status tidak valid.");
        }
        BigDecimal price = parsePrice(old.get("price_per_day"));
        if (price == null) {
            errors.add("Tarif harian harus berupa angka minimal 0.");
        }
        int hp = Web.parseInt(old.get("hp"), 0);
        if (hp < 1) {
            errors.add("Engine power harus berupa angka minimal 1.");
        }
        int topSpeed = Web.parseInt(old.get("top_speed"), 0);
        if (topSpeed < 1) {
            errors.add("Top speed harus berupa angka minimal 1.");
        }

        Part upload = req.getPart("image");
        boolean hasUpload = upload != null && upload.getSize() > 0;
        String extension = null;
        if (hasUpload) {
            extension = detectImageExtension(upload);
            if (extension == null) {
                errors.add("Gambar harus berformat JPG, PNG, atau WEBP.");
            }
        } else if (creating) {
            errors.add("Gambar kendaraan wajib diunggah.");
        }

        if (!errors.isEmpty()) {
            Web.flashErrors(req, errors);
            Web.flashOld(req, old);
            Web.redirect(req, res, formPath);
            return;
        }

        Car car = creating ? new Car() : existing;
        car.setName(old.get("name"));
        car.setBrand(old.get("brand"));
        car.setCategory(old.get("category"));
        car.setStatus(old.get("status"));
        car.setPricePerDay(price);
        car.setHp(hp);
        car.setTopSpeed(topSpeed);

        if (hasUpload) {
            String stored = "cars/" + UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path target = AppConfig.uploadDir().resolve(stored).normalize();
            Files.createDirectories(target.getParent());
            try (InputStream in = upload.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!creating) {
                deleteImage(existing.getImage());
            }
            car.setImage(stored);
        }

        if (creating) {
            cars.create(car);
            Web.flashSuccess(req, "Kendaraan berhasil ditambahkan.");
        } else {
            cars.update(car);
            Web.flashSuccess(req, "Data kendaraan berhasil diperbarui.");
        }
        Web.redirect(req, res, "/admin/cars");
    }

    private static BigDecimal parsePrice(String value) {
        try {
            BigDecimal price = new BigDecimal(value);
            return price.signum() < 0 || price.precision() > 12 ? null : price;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String detectImageExtension(Part part) throws IOException {
        byte[] head = new byte[12];
        int read;
        try (InputStream in = part.getInputStream()) {
            read = in.readNBytes(head, 0, head.length);
        }
        if (read >= 3 && (head[0] & 0xFF) == 0xFF && (head[1] & 0xFF) == 0xD8 && (head[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (read >= 8 && (head[0] & 0xFF) == 0x89 && head[1] == 'P' && head[2] == 'N' && head[3] == 'G') {
            return "png";
        }
        if (read >= 12 && head[0] == 'R' && head[1] == 'I' && head[2] == 'F' && head[3] == 'F'
                && head[8] == 'W' && head[9] == 'E' && head[10] == 'B' && head[11] == 'P') {
            return "webp";
        }
        return null;
    }

    private static void deleteImage(String stored) {
        if (stored == null || !STORED_IMAGE.matcher(stored).matches() || stored.contains("..")) {
            return;
        }
        Path root = AppConfig.uploadDir();
        Path file = root.resolve(stored).normalize();
        if (file.startsWith(root)) {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) {
                return;
            }
        }
    }
}
