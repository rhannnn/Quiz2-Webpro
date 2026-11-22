# Quiz 2 Web programming

Website Luxury Rent Car & Motorsport, dipindahkan dari Laravel ke **Java/Jakarta Server Pages**.

Stack: Servlet 6 + JSP 3.1 + JSTL 3 (Jakarta EE 10), JDBC + HikariCP, MySQL/MariaDB. Berjalan di **Tomcat 10.1** dengan Java 17 atau lebih baru.

## Fitur

Semua fitur versi Laravel: register/login/logout (role admin dan customer), katalog dengan filter, pencarian, urutan harga dan pagination, detail kendaraan, booking dengan hitung total dan cek bentrok tanggal, riwayat dan pembatalan booking, testimoni, serta panel admin (dashboard, CRUD kendaraan dengan upload gambar, manajemen status booking).

Fitur tambahan:

1. **Favorit**: customer menyimpan kendaraan dengan ikon hati, lengkap dengan halaman `/favorites` (tabel baru `favorites`).
2. **Invoice booking**: halaman invoice yang bisa dicetak atau disimpan sebagai PDF, dari "My Bookings" (customer) dan "VIP Manifest" (admin).

## Menjalankan di komputer

Prasyarat: JDK 17+, Maven 3.9+, Tomcat 10.1, MySQL/MariaDB menyala (XAMPP).

```
mvn clean package
```

Salin `target/prestige.war` ke folder `webapps` Tomcat (ganti nama menjadi `ROOT.war` agar tampil di `http://localhost:8080/`), lalu jalankan Tomcat.

Saat pertama jalan, aplikasi membuat database `luxury_rent_car_jsp`, semua tabel, dan data awal sendiri. `database/schema.sql` hanya sebagai referensi bila ingin impor manual lewat phpMyAdmin.

Akun awal:

| Role | Email | Password |
| --- | --- | --- |
| Admin | admin@prestige.test | admin12345 |
| Customer | customer@prestige.test | customer12345 |

Ganti password ini lewat variabel `ADMIN_PASSWORD` dan `CUSTOMER_PASSWORD` sebelum aplikasi dipublikasikan.

## Konfigurasi (environment variable atau `-D` pada JAVA_OPTS)

| Variabel | Default |
| --- | --- |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | localhost / 3306 / luxury_rent_car_jsp |
| `DB_USER` / `DB_PASSWORD` | root / (kosong) |
| `DB_URL` | URL JDBC lengkap, menggantikan tiga variabel host di atas |
| `DB_POOL` | 5 |
| `UPLOAD_DIR` | `~/prestige-uploads` |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | admin@prestige.test / admin12345 |
| `CUSTOMER_EMAIL` / `CUSTOMER_PASSWORD` | customer@prestige.test / customer12345 |

Database lama Laravel tidak dipakai bersama karena format hash password berbeda. Aplikasi ini memakai database baru.

## Struktur

```
src/main/java/com/prestige
  config/  AppConfig
  db/      Database, SchemaInit, Sql
  model/   User, Car, Booking, Testimonial, Page
  dao/     UserDao, CarDao, BookingDao, TestimonialDao, FavoriteDao
  util/    Passwords, Fmt (fungsi EL)
  web/     AppFilter (CSRF, sesi, akses), AppListener, servlet
src/main/webapp/WEB-INF
  views/   halaman JSP (di WEB-INF supaya hanya bisa diakses lewat servlet)
  tags/    layout, adminLayout, carCard, pagination, flash (tag file)
  jspf/    head.jspf
  prestige.tld  fungsi EL: h, js, idr, date, repeat, today, year
```

## Docker

`Dockerfile` membangun WAR dan menjalankannya di Tomcat 10.1. Port mengikuti variabel `PORT` bila disediakan platform.
