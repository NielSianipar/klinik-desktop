# Panduan Dokumentasi & Alur Kerja Sistem Manajemen Klinik

Dokumentasi ini menjelaskan secara komprehensif arsitektur teknologi, struktur database, alur kerja operasional (workflow), serta fitur-fitur yang ada pada aplikasi desktop **Sistem Manajemen Klinik Sehat USTB** (berbasis JavaFX dan MySQL).

---

## 🛠️ 1. Arsitektur & Tumpukan Teknologi (Tech Stack)

Aplikasi ini dibangun menggunakan arsitektur **MVC (Model-View-Controller)** yang dikombinasikan dengan pola **DAO (Data Access Object)** untuk memisahkan logika tampilan, kendali alur, dan interaksi database secara bersih.

- **UI (FXML View / Antarmuka JavaFX):** Berupa file XML JavaFX (.fxml) yang menentukan struktur visual dari halaman login, dashboard, tabel data pasien, rekam medis, dll.
- **Controller (JavaFX Controller):** Logika Java yang menangani interaksi pengguna pada UI (klik tombol, pengetikan form) dan menghubungkannya dengan database melalui DAO.
- **Model (Java Models / Entities):** Objek Java murni (POJO) yang memetakan baris tabel database menjadi objek Java (misal kelas `Pasien`, `Dokter`, `Obat`).
- **DAO (Data Access Object Layer):** Kelas Java khusus yang menampung query SQL (Insert, Update, Select, Delete) untuk berkomunikasi langsung dengan database MySQL.
- **Database MySQL:** RDBMS penyimpanan data persisten seluruh tabel operasional klinik.

### Komponen Teknologi:
*   **Java Development Kit (JDK 17):** Bahasa pemrograman utama yang digunakan untuk backend aplikasi.
*   **JavaFX 21:** Framework GUI untuk membangun jendela desktop, layout, tabel, dan form input yang dinamis.
*   **Maven:** Tool otomatisasi build dan manajemen dependensi (dikonfigurasi dalam `pom.xml`).
*   **MySQL Database (v8.x):** Sistem Manajemen Database Relasional (RDBMS) untuk menyimpan seluruh rekaman data klinik.
*   **JDBC (Java Database Connectivity):** Driver penghubung langsung dari Java ke MySQL.
*   **BCrypt (org.mindrot.jcheckstyle):** Pustaka enkripsi satu arah (hashing) untuk mengamankan password akun pengguna di database.
*   **CSS (style.css):** Styling kustom premium menggunakan tema modern yang terinspirasi oleh **Mazer Admin Dashboard**.

---

## 📊 2. Skema & Struktur Database Relasional

Sistem menggunakan database bernama `db_klinik` yang terdiri dari tabel-tabel utama berikut:

1.  **`pengguna`**: Menyimpan data akun login staf klinik (Admin, Dokter, Apoteker, Resepsionis) dengan password terenkripsi BCrypt.
2.  **`spesialisasi`**: Daftar spesialisasi dokter (contoh: Dokter Umum, Dokter Gigi, Dokter Anak).
3.  **`dokter`**: Informasi detail profil dokter (nomor SIP, telepon, bio) yang terhubung ke tabel `pengguna` dan `spesialisasi`.
4.  **`jadwal_dokter`**: Waktu dan hari operasional dokter beserta limit kuota harian.
5.  **`pasien`**: Data pribadi lengkap pasien termasuk nomor Rekam Medis (RM) unik dan daftar alergi obat.
6.  **`pendaftaran`**: Antrian kunjungan pasien pada tanggal tertentu yang menunjuk ke dokter spesialis tertentu.
7.  **`rekam_medis`**: Hasil diagnosis medis dokter setelah memeriksa pasien, termasuk tanda vital dan catatan pengobatan.
8.  **`obat`**: Inventori stok apotek, harga, dan tanggal kedaluwarsa.
9.  **`resep` & `resep_detail`**: Penghubung rekam medis dengan obat-obatan yang diresepkan dan aturan pakainya.

---

## 🔄 3. Alur Kerja Utama Sistem (Sistem Workflow)

Aplikasi memiliki alur terintegrasi (end-to-end) mulai dari kedatangan pasien hingga penyelesaian pemeriksaan medis dan pengambilan obat:

### Langkah 1: Autentikasi Pengguna (Login)
*   Pengguna memasukkan **Email** dan **Password**.
*   Sistem mencocokkan email di database via `PenggunaDAO`.
*   Sistem melakukan verifikasi password menggunakan fungsi `BCrypt.checkpw()`.
*   Jika berhasil, sistem memperbarui waktu masuk terakhir pengguna pada kolom `terakhir_masuk` (aman dari kegagalan skema berkat fallback query) dan membuka menu utama sesuai hak akses.

### Langkah 2: Registrasi Pasien
*   Resepsionis mendaftarkan pasien baru di menu **Data Pasien**.
*   Nomor RM (Rekam Medis) di-generate secara otomatis oleh sistem (format: `RM-YYYYMMDD-XXXX`).

### Langkah 3: Pendaftaran Antrian
*   Pasien didaftarkan ke dokter pilihan melalui menu **Pendaftaran Antrian**.
*   Sistem secara otomatis menghitung nomor antrian berjalan hari ini untuk dokter terkait (mulai dari `1`, `2`, `3` dst.).
*   Status pendaftaran default diatur ke **"menunggu"**.

### Langkah 4: Pemeriksaan & Diagnosa oleh Dokter
*   Dokter masuk ke menu **Rekam Medis**.
*   Di panel kiri, dokter dapat melihat daftar antrian pasien yang sedang menunggu giliran hari ini.
*   Saat pasien dipilih, data ringkasan alergi dan usia pasien langsung ditampilkan di bagian atas form.
*   Dokter mengisi keluhan (*anamnesis*), pemeriksaan fisik, tanda-tanda vital (Tekanan Darah, Suhu, BB, TB), diagnosis utama, kode ICD-10, serta menuliskan daftar resep obat yang diperlukan.

### Langkah 5: Transaksi Pengurangan Stok & Selesai Antrian
*   Ketika dokter mengklik **"Simpan Pemeriksaan & Selesaikan"**:
    1.  Data disimpan ke tabel `rekam_medis`.
    2.  Resep obat dicatat di tabel `resep` dan detailnya di `resep_detail`.
    3.  **Pengurangan Stok Apotek:** Sistem secara otomatis memotong jumlah stok masing-masing obat yang diresepkan di tabel `obat`.
    4.  Status antrian pasien diperbarui dari **"menunggu"** / **"diperiksa"** menjadi **"selesai"**.

---

## 🌟 4. Penjelasan Detail Fitur Aplikasi

### 🏠 A. Ringkasan Dashboard (Overview)
Menampilkan metrik performa klinik hari ini secara real-time yang bersumber dari database:
*   **Total Pasien:** Menghitung total seluruh pasien terdaftar.
*   **Antrian Hari Ini:** Jumlah total antrian pasien yang masuk pada tanggal hari ini.
*   **Stok Obat Menipis:** Indikator penting yang mendeteksi obat dengan sisa stok `< 10` unit agar segera dilakukan restock.
*   **Dokter Aktif:** Jumlah total dokter yang terdaftar di klinik.
*   **Tabel Antrian Terkini:** Menampilkan daftar pasien yang antri hari ini secara transparan beserta statusnya.

### 👥 B. Manajemen Data Pasien
*   Pencarian data pasien secara instan berdasarkan nama atau nomor RM.
*   Form tambah dan edit data pasien secara lengkap (NIK, Nama, Kelamin, Tanggal Lahir, Telepon, Alamat, Alergi Obat).

### 👨‍⚕️ C. Dokter & Jadwal Praktik
*   **Profil Dokter:** Menghubungkan akun bertipe peran 'dokter' ke informasi SIP (Surat Izin Praktik), nomor telepon, biografi ringkas, dan spesialisasi medisnya.
*   **Jadwal Dokter:** Mengatur hari praktik dokter (Senin - Minggu), batas kuota pasien harian, dan jam mulai/selesai pelayanan.

### 📋 D. Pendaftaran Antrian
*   Mempermudah penambahan antrian secara teratur.
*   Sistem validasi otomatis yang mencegah pendaftaran pasien berulang pada hari yang sama untuk dokter yang sama.
*   Opsi untuk mengubah status antrian secara manual (Menunggu, Diperiksa, Selesai, Batal).

### 🩺 E. Modul Rekam Medis (Medical Records)
*   **Form Pemeriksaan Baru:** Memudahkan dokter memasukkan rekam medis secara menyeluruh dan cepat dalam satu halaman terpadu.
*   **Riwayat Pemeriksaan Pasien:** Dokter dapat memantau riwayat kunjungan masa lalu pasien secara lengkap (tanggal, dokter yang menangani, diagnosis sebelumnya, dan resep obat yang pernah diberikan) untuk membantu menegakkan diagnosis yang konsisten.

### 💊 F. Apotek & Obat
*   Manajemen inventori obat secara akurat (Kode Obat, Nama, Jenis, Sisa Stok, Harga Jual, Tanggal Expired).
*   Obat yang kedaluwarsa atau stoknya kosong akan memunculkan peringatan otomatis bagi Apoteker.

### 📊 G. Laporan Kunjungan (Reporting)
*   Menyediakan laporan historis kunjungan pasien.
*   Dilengkapi filter pencarian berdasarkan rentang tanggal tertentu guna memudahkan manajemen klinik memantau statistik kunjungan bulanan atau tahunan.

---

## 🔒 5. Fitur Keamanan & Skema Toleransi Error (Fallback)

1.  **Enkripsi Hashing BCrypt:**
    Password pengguna disimpan dalam bentuk hash tidak dapat dibalik. Setiap upaya login diverifikasi menggunakan algoritma BCrypt yang kuat terhadap serangan brute-force.
2.  **Skema Toleransi Nama Kolom (Fallback Schema Support):**
    Untuk mengantisipasi ketidaksamaan nama kolom `diperbarui` (pada versi lama) dan `diperbarui_pada` (pada versi baru) pada database MySQL lokal, seluruh kelas DAO (`PenggunaDAO`, `PasienDAO`, `ObatDAO`, `PendaftaranDAO`, `RekamMedisDAO`) telah dilengkapi metode penangkap error (`SQLException` handling). Sistem akan secara otomatis mengalihkan query ke kolom cadangan jika kolom utama tidak ditemukan, sehingga aplikasi dijamin tidak akan mengalami crash di sistem manapun.
3.  **Toleransi Deteksi Fokus CSS:**
    Menggunakan pengaturan CSS khusus untuk meredam focus-ring bawaan platform JavaFX sehingga tampilan dashboard tetap bersih tanpa kotak garis ganda yang tidak diinginkan di layar pengguna.
