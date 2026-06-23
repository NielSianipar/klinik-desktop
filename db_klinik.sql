-- ============================================================
--  Sistem Manajemen Klinik - Database Schema
--  File: db_klinik.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_klinik;
USE db_klinik;

-- 1. Tabel pengguna
CREATE TABLE IF NOT EXISTS pengguna (
    id_pengguna INT AUTO_INCREMENT PRIMARY KEY,
    nama_lengkap VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    kata_sandi VARCHAR(255) NOT NULL,
    peran ENUM('admin', 'dokter', 'perawat', 'pasien') NOT NULL,
    aktif TINYINT(1) DEFAULT 1,
    terakhir_masuk DATETIME NULL,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabel spesialisasi
CREATE TABLE IF NOT EXISTS spesialisasi (
    id_spesialisasi INT AUTO_INCREMENT PRIMARY KEY,
    nama_spesialisasi VARCHAR(100) NOT NULL,
    keterangan TEXT
);

-- 3. Tabel dokter
CREATE TABLE IF NOT EXISTS dokter (
    id_dokter INT AUTO_INCREMENT PRIMARY KEY,
    id_pengguna INT UNIQUE NOT NULL,
    id_spesialisasi INT NOT NULL,
    nomor_sip VARCHAR(50) UNIQUE NOT NULL,
    telepon VARCHAR(20),
    bio TEXT,
    foto VARCHAR(255),
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pengguna) REFERENCES pengguna(id_pengguna) ON DELETE CASCADE,
    FOREIGN KEY (id_spesialisasi) REFERENCES spesialisasi(id_spesialisasi)
);

-- 4. Tabel pasien
CREATE TABLE IF NOT EXISTS pasien (
    id_pasien INT AUTO_INCREMENT PRIMARY KEY,
    nomor_rm VARCHAR(20) UNIQUE NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    tanggal_lahir DATE,
    jenis_kelamin ENUM('Laki-laki', 'Perempuan'),
    telepon VARCHAR(20),
    email VARCHAR(100),
    golongan_darah ENUM('A', 'B', 'AB', 'O'),
    riwayat_alergi TEXT,
    alamat TEXT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diperbarui TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. Tabel jadwal_dokter
CREATE TABLE IF NOT EXISTS jadwal_dokter (
    id_jadwal INT AUTO_INCREMENT PRIMARY KEY,
    id_dokter INT NOT NULL,
    hari ENUM('senin', 'selasa', 'rabu', 'kamis', 'jumat', 'sabtu', 'minggu') NOT NULL,
    jam_mulai TIME NOT NULL,
    jam_selesai TIME NOT NULL,
    kuota INT NOT NULL,
    tersedia TINYINT(1) DEFAULT 1,
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter) ON DELETE CASCADE
);

-- 6. Tabel pendaftaran
CREATE TABLE IF NOT EXISTS pendaftaran (
    id_pendaftaran INT AUTO_INCREMENT PRIMARY KEY,
    id_pasien INT NOT NULL,
    id_dokter INT NOT NULL,
    id_jadwal INT NOT NULL,
    tanggal_kunjungan DATE NOT NULL,
    nomor_antrian VARCHAR(10) NOT NULL,
    status ENUM('menunggu', 'dipanggil', 'selesai', 'batal') DEFAULT 'menunggu',
    keluhan TEXT,
    catatan_admin TEXT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diperbarui TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pasien) REFERENCES pasien(id_pasien),
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter),
    FOREIGN KEY (id_jadwal) REFERENCES jadwal_dokter(id_jadwal)
);

-- 7. Tabel rekam_medis
CREATE TABLE IF NOT EXISTS rekam_medis (
    id_rekam_medis INT AUTO_INCREMENT PRIMARY KEY,
    id_pendaftaran INT UNIQUE NOT NULL,
    id_dokter INT NOT NULL,
    id_pasien INT NOT NULL,
    tanggal_periksa DATE NOT NULL,
    tekanan_darah VARCHAR(20),
    suhu_tubuh DECIMAL(4,2),
    berat_badan DECIMAL(5,2),
    tinggi_badan DECIMAL(5,2),
    anamnesis TEXT NOT NULL,
    pemeriksaan_fisik TEXT,
    diagnosis TEXT NOT NULL,
    kode_icd VARCHAR(20),
    tindakan TEXT,
    catatan TEXT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diperbarui TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pendaftaran) REFERENCES pendaftaran(id_pendaftaran),
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter),
    FOREIGN KEY (id_pasien) REFERENCES pasien(id_pasien)
);

-- 8. Tabel obat
CREATE TABLE IF NOT EXISTS obat (
    id_obat INT AUTO_INCREMENT PRIMARY KEY,
    kode_obat VARCHAR(50) UNIQUE NOT NULL,
    nama_obat VARCHAR(100) NOT NULL,
    kategori VARCHAR(100),
    satuan VARCHAR(50),
    stok INT NOT NULL DEFAULT 0,
    harga DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    keterangan TEXT,
    aktif TINYINT(1) DEFAULT 1,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diperbarui TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 9. Tabel riwayat_pengobatan
CREATE TABLE IF NOT EXISTS riwayat_pengobatan (
    id_riwayat INT AUTO_INCREMENT PRIMARY KEY,
    id_rekam_medis INT NOT NULL,
    id_obat INT NOT NULL,
    jumlah INT NOT NULL,
    aturan_pakai VARCHAR(150),
    catatan_apoteker TEXT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rekam_medis) REFERENCES rekam_medis(id_rekam_medis) ON DELETE CASCADE,
    FOREIGN KEY (id_obat) REFERENCES obat(id_obat)
);

-- 10. Tabel laporan_kunjungan
CREATE TABLE IF NOT EXISTS laporan_kunjungan (
    id_laporan INT AUTO_INCREMENT PRIMARY KEY,
    id_dokter INT NOT NULL,
    periode_mulai DATE NOT NULL,
    periode_selesai DATE NOT NULL,
    total_kunjungan INT NOT NULL,
    total_pasien_baru INT NOT NULL,
    keterangan TEXT,
    dibuat_oleh INT,
    dibuat_pada TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_dokter) REFERENCES dokter(id_dokter),
    FOREIGN KEY (dibuat_oleh) REFERENCES pengguna(id_pengguna)
);

-- ============================================================
--  Seed Data Awal untuk Demo (Password: admin123 Hashed BCrypt)
-- ============================================================
INSERT INTO pengguna (nama_lengkap, email, kata_sandi, peran, aktif) VALUES 
('Administrator Utama', 'admin@klinik.com', '$2a$10$wKxN74hW.e0F6cW8rU.wLu.xI9w04lI2eP.yKsp37kX.jK0.U4bPe', 'admin', 1),
('Dr. Budi Santoso, Sp.A', 'budi@klinik.com', '$2a$10$wKxN74hW.e0F6cW8rU.wLu.xI9w04lI2eP.yKsp37kX.jK0.U4bPe', 'dokter', 1),
('Siti Aminah, Amd.Kep', 'siti@klinik.com', '$2a$10$wKxN74hW.e0F6cW8rU.wLu.xI9w04lI2eP.yKsp37kX.jK0.U4bPe', 'perawat', 1);

INSERT INTO spesialisasi (nama_spesialisasi, keterangan) VALUES 
('Umum', 'Dokter umum untuk pelayanan kesehatan primer keluarga'),
('Anak', 'Spesialisasi kesehatan bayi, anak-anak, dan remaja'),
('Gigi', 'Spesialisasi pemeriksaan dan perawatan gigi dan mulut');

INSERT INTO dokter (id_pengguna, id_spesialisasi, nomor_sip, telepon, bio) VALUES 
(2, 2, 'SIP/100/A/2026/ANAK', '081234567890', 'Dokter anak ramah dengan spesialisasi nutrisi dan tumbuh kembang balita.');

INSERT INTO obat (kode_obat, nama_obat, kategori, satuan, stok, harga, keterangan) VALUES 
('OBT-001', 'Paracetamol Syrup 60ml', 'Obat Bebas', 'Botol', 50, 18500.00, 'Pereda demam anak rasa jeruk'),
('OBT-002', 'Amoxicillin 500mg', 'Obat Keras', 'Tablet', 8, 12000.00, 'Antibiotik infeksi bakteri pernafasan (Stok Menipis)'),
('OBT-003', 'Cetirizine 10mg', 'Obat Bebas Terbatas', 'Tablet', 120, 7500.00, 'Antihistamin pereda alergi');

-- Seed Jadwal Dokter untuk dr. Budi Santoso, Sp.A (Dokter ID: 1)
INSERT INTO jadwal_dokter (id_dokter, hari, jam_mulai, jam_selesai, kuota, tersedia) VALUES 
(1, 'senin', '08:00:00', '12:00:00', 20, 1),
(1, 'selasa', '08:00:00', '12:00:00', 20, 1),
(1, 'rabu', '08:00:00', '12:00:00', 20, 1),
(1, 'kamis', '13:00:00', '17:00:00', 15, 1),
(1, 'jumat', '13:00:00', '17:00:00', 15, 1);

