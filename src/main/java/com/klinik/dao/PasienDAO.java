package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.Pasien;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: pasien
 * 
 * @author Daniel sianipar
 */
public class PasienDAO {

    /**
     * Mencari pasien berdasarkan ID.
     */
    public Pasien findById(int id) {
        String sql = "SELECT * FROM pasien WHERE id_pasien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPasien(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari pasien berdasarkan ID Pengguna.
     */
    public Pasien findByPenggunaId(int idPengguna) {
        String sql = "SELECT * FROM pasien WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPasien(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari pasien berdasarkan email.
     */
    public Pasien findByEmail(String email) {
        String sql = "SELECT * FROM pasien WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPasien(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari pasien berdasarkan nomor rekam medis (RM).
     */
    public Pasien findByNomorRm(String nomorRm) {
        String sql = "SELECT * FROM pasien WHERE nomor_rm = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nomorRm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPasien(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Otomatis membuat nomor rekam medis (RM) baru dengan format RM-XXXX (contoh: RM-0001).
     */
    public String generateNomorRm() {
        String sql = "SELECT nomor_rm FROM pasien ORDER BY id_pasien DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String lastRm = rs.getString("nomor_rm");
                if (lastRm != null && lastRm.startsWith("RM-")) {
                    try {
                        int num = Integer.parseInt(lastRm.substring(3));
                        return String.format("RM-%04d", num + 1);
                    } catch (NumberFormatException e) {
                        // Jika format tidak sesuai, lewati ke default
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "RM-0001";
    }

    /**
     * Menambahkan pasien baru ke database.
     */
    public boolean insert(Pasien p) {
        String sql = "INSERT INTO pasien (id_pengguna, nomor_rm, nama_lengkap, tanggal_lahir, jenis_kelamin, alamat, telepon, email, golongan_darah, riwayat_alergi) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            if (p.getIdPengguna() != null) {
                ps.setInt(1, p.getIdPengguna());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, p.getNomorRm());
            ps.setString(3, p.getNamaLengkap());
            ps.setDate(4, p.getTanggalLahir() != null ? Date.valueOf(p.getTanggalLahir()) : null);
            ps.setString(5, p.getJenisKelamin());
            ps.setString(6, p.getAlamat());
            ps.setString(7, p.getTelepon());
            ps.setString(8, p.getEmail());
            ps.setString(9, p.getGolonganDarah());
            ps.setString(10, p.getRiwayatAlergi());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPasien(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Memperbarui data pasien.
     */
    public boolean update(Pasien p) {
        String sql = "UPDATE pasien SET id_pengguna = ?, nomor_rm = ?, nama_lengkap = ?, tanggal_lahir = ?, jenis_kelamin = ?, alamat = ?, telepon = ?, email = ?, golongan_darah = ?, riwayat_alergi = ? WHERE id_pasien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            if (p.getIdPengguna() != null) {
                ps.setInt(1, p.getIdPengguna());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, p.getNomorRm());
            ps.setString(3, p.getNamaLengkap());
            ps.setDate(4, p.getTanggalLahir() != null ? Date.valueOf(p.getTanggalLahir()) : null);
            ps.setString(5, p.getJenisKelamin());
            ps.setString(6, p.getAlamat());
            ps.setString(7, p.getTelepon());
            ps.setString(8, p.getEmail());
            ps.setString(9, p.getGolonganDarah());
            ps.setString(10, p.getRiwayatAlergi());
            ps.setInt(11, p.getIdPasien());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menghapus pasien berdasarkan ID.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM pasien WHERE id_pasien = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mengambil semua daftar pasien.
     */
    public List<Pasien> findAll() {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM pasien ORDER BY nomor_rm ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToPasien(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari pasien berdasarkan nama, nomor RM, atau telepon (untuk pencarian dinamis di tabel).
     */
    public List<Pasien> search(String query) {
        List<Pasien> list = new ArrayList<>();
        String sql = "SELECT * FROM pasien WHERE nama_lengkap LIKE ? OR nomor_rm LIKE ? OR telepon LIKE ? ORDER BY nomor_rm ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String keyword = "%" + query + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPasien(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Pasien mapRowToPasien(ResultSet rs) throws SQLException {
        Pasien p = new Pasien();
        p.setIdPasien(rs.getInt("id_pasien"));
        
        int idPenggunaVal = rs.getInt("id_pengguna");
        p.setIdPengguna(rs.wasNull() ? null : idPenggunaVal);
        
        p.setNomorRm(rs.getString("nomor_rm"));
        p.setNamaLengkap(rs.getString("nama_lengkap"));
        
        Date dob = rs.getDate("tanggal_lahir");
        if (dob != null) {
            p.setTanggalLahir(dob.toLocalDate());
        }
        
        p.setJenisKelamin(rs.getString("jenis_kelamin"));
        p.setAlamat(rs.getString("alamat"));
        p.setTelepon(rs.getString("telepon"));
        p.setEmail(rs.getString("email"));
        p.setGolonganDarah(rs.getString("golongan_darah"));
        p.setRiwayatAlergi(rs.getString("riwayat_alergi"));
        
        Timestamp created = rs.getTimestamp("dibuat_pada");
        if (created != null) {
            p.setDibuatPada(created.toLocalDateTime());
        }
        Timestamp updated = null;
        try {
            updated = rs.getTimestamp("diperbarui");
        } catch (SQLException e) {
            try {
                updated = rs.getTimestamp("diperbarui_pada");
            } catch (SQLException ex) {}
        }
        if (updated != null) {
            p.setDiperbarui(updated.toLocalDateTime());
        }
        return p;
    }
}
