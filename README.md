# 🏥 Sistem Manajemen Klinik (Klinik Desktop)

Aplikasi desktop Sistem Manajemen Klinik berbasis **JavaFX 21**, **Maven**, dan **MySQL** database. Dilengkapi dengan standard keamanan enkripsi **jBCrypt** untuk autentikasi user dan alur data transaksi klinik lengkap.

---

## 🛠️ Persyaratan Sistem
* **Java Development Kit (JDK) 17** atau yang lebih tinggi.
* **Apache Maven** (terdaftar di system environment PATH).
* **MySQL Server** (XAMPP / MySQL Installer).

---

## 📂 Struktur Utama Proyek
* `com.klinik.model`: Representasi object database (Pengguna, Pasien, Dokter, Obat, dll).
* `com.klinik.dao`: Layer koneksi SQL JDBC data access objects (CRUD database).
* `com.klinik.controller`: Logika kontroler UI JavaFX dan penanganan event FXML.
* `com.klinik.util`: Utility pendukung (SessionManager, AlertHelper).
* `src/main/resources/fxml`: Layout desain visual (.fxml) sub-halaman aplikasi.
* `src/main/resources/css`: Style layout global (.css).

---

## 🚀 Langkah Instalasi & Menjalankan Aplikasi

### 1. Inisialisasi Database
1. Pastikan server MySQL Anda aktif (misalnya melalui XAMPP).
2. Impor file schema `db_klinik.sql` di MySQL CLI atau phpMyAdmin:
   ```sql
   source path/to/db_klinik.sql;
   ```
   *Atau buat database `db_klinik` lalu jalankan isi file `db_klinik.sql` di tool SQL GUI (DBeaver/Navicat).*

### 2. Konfigurasi Koneksi
Buka file `src/main/resources/database.properties` dan sesuaikan username serta password MySQL Anda:
```properties
db.host=localhost
db.port=3306
db.name=db_klinik
db.username=root
db.password=yourpassword
```

### 3. Kompilasi & Jalankan Aplikasi
Jalankan perintah Maven berikut di terminal utama direktori proyek:
```bash
# Bersihkan dan kompilasi project
mvn clean compile

# Jalankan aplikasi JavaFX
mvn javafx:run
```

---

## 🔐 Akun Percobaan (Demo)
Gunakan kredensial berikut untuk menguji alur role-based access control (Password bawaan untuk semua akun adalah: `admin123`):

| Peran (Role) | Email | Deskripsi |
| :--- | :--- | :--- |
| **Admin** | `admin@klinik.com` | Memiliki akses penuh ke semua modul sistem. |
| **Dokter** | `budi@klinik.com` | Mengelola rekam medis pemeriksaan & resep obat pasien. |
| **Perawat** | `siti@klinik.com` | Pendaftaran antrian, check-in pasien baru, & jadwal praktik. |

---

## 💡 Fitur Utama Alur Pemeriksaan Medis
1. **Perawat** mendaftarkan pasien baru/lama ke antrian dokter di menu **Pendaftaran Antrian**.
2. Status antrian diset otomatis ke `menunggu`.
3. **Dokter** masuk ke sistem dan melihat daftar antrian miliknya di menu **Rekam Medis**.
4. Dokter mengeklik tombol `Panggil` (status berubah ke `dipanggil`) untuk memeriksa pasien.
5. Dokter menginput data vitals, diagnosa, dan **Resep Obat**.
6. Saat mengeklik `Simpan Pemeriksaan`, sistem akan secara otomatis:
   * Menyimpan data rekam medis.
   * Menyimpan detail item resep.
   * **Memotong stok obat** secara otomatis di apotek.
   * Mengubah status antrian menjadi `selesai`.
7. Riwayat checkup dan stok apotek diperbarui secara realtime di dasbor utama!
