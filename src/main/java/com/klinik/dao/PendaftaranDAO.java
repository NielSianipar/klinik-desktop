package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.Pendaftaran;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: pendaftaran (antrian pemeriksaan)
 * 
 * @author Daniel sianipar
 */
public class PendaftaranDAO {

    /**
     * Mengambil semua data pendaftaran dengan relasi nama pasien, nomor RM, nama dokter dan spesialisasi.
     */
    public List<Pendaftaran> findAll() {
        List<Pendaftaran> list = new ArrayList<>();
        String sql = "SELECT p.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM pendaftaran p " +
                     "JOIN pasien pas ON p.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON p.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "ORDER BY p.tanggal_kunjungan DESC, p.nomor_antrian ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToPendaftaran(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari pendaftaran berdasarkan ID.
     */
    public Pendaftaran findById(int id) {
        String sql = "SELECT p.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM pendaftaran p " +
                     "JOIN pasien pas ON p.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON p.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE p.id_pendaftaran = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPendaftaran(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mengambil daftar pendaftaran berdasarkan tanggal kunjungan.
     */
    public List<Pendaftaran> findByDate(LocalDate tanggal) {
        List<Pendaftaran> list = new ArrayList<>();
        String sql = "SELECT p.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM pendaftaran p " +
                     "JOIN pasien pas ON p.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON p.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE p.tanggal_kunjungan = ? " +
                     "ORDER BY p.nomor_antrian ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPendaftaran(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mengambil daftar pendaftaran berdasarkan dokter dan tanggal.
     */
    public List<Pendaftaran> findByDokterAndDate(int idDokter, LocalDate tanggal) {
        List<Pendaftaran> list = new ArrayList<>();
        String sql = "SELECT p.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM pendaftaran p " +
                     "JOIN pasien pas ON p.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON p.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE p.id_dokter = ? AND p.tanggal_kunjungan = ? " +
                     "ORDER BY p.nomor_antrian ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDokter);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToPendaftaran(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menghasilkan nomor antrian otomatis berdasarkan jumlah pendaftaran pada dokter dan tanggal yang sama.
     */
    public String generateNomorAntrian(int idDokter, LocalDate tanggal) {
        String sql = "SELECT COUNT(*) FROM pendaftaran WHERE id_dokter = ? AND tanggal_kunjungan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDokter);
            ps.setDate(2, Date.valueOf(tanggal));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return String.format("%03d", count + 1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "001";
    }

    /**
     * Menambahkan data pendaftaran (pendaftaran antrian) baru.
     */
    public boolean insert(Pendaftaran p) {
        String sql = "INSERT INTO pendaftaran (id_pasien, id_dokter, id_jadwal, tanggal_kunjungan, nomor_antrian, status, keluhan, catatan_admin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getIdPasien());
            ps.setInt(2, p.getIdDokter());
            ps.setInt(3, p.getIdJadwal());
            ps.setDate(4, Date.valueOf(p.getTanggalKunjungan()));
            ps.setString(5, p.getNomorAntrian());
            ps.setString(6, p.getStatus()); // default: menunggu
            ps.setString(7, p.getKeluhan());
            ps.setString(8, p.getCatatanAdmin());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdPendaftaran(rs.getInt(1));
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
     * Memperbarui status atau data pendaftaran.
     */
    public boolean update(Pendaftaran p) {
        String sql = "UPDATE pendaftaran SET id_pasien = ?, id_dokter = ?, id_jadwal = ?, tanggal_kunjungan = ?, nomor_antrian = ?, status = ?, keluhan = ?, catatan_admin = ? WHERE id_pendaftaran = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getIdPasien());
            ps.setInt(2, p.getIdDokter());
            ps.setInt(3, p.getIdJadwal());
            ps.setDate(4, Date.valueOf(p.getTanggalKunjungan()));
            ps.setString(5, p.getNomorAntrian());
            ps.setString(6, p.getStatus());
            ps.setString(7, p.getKeluhan());
            ps.setString(8, p.getCatatanAdmin());
            ps.setInt(9, p.getIdPendaftaran());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Memperbarui status pendaftaran (menunggu | dipanggil | selesai | batal).
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE pendaftaran SET status = ? WHERE id_pendaftaran = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Pendaftaran mapRowToPendaftaran(ResultSet rs) throws SQLException {
        Pendaftaran p = new Pendaftaran();
        p.setIdPendaftaran(rs.getInt("id_pendaftaran"));
        p.setIdPasien(rs.getInt("id_pasien"));
        p.setIdDokter(rs.getInt("id_dokter"));
        p.setIdJadwal(rs.getInt("id_jadwal"));
        
        Date visitDate = rs.getDate("tanggal_kunjungan");
        if (visitDate != null) {
            p.setTanggalKunjungan(visitDate.toLocalDate());
        }
        
        p.setNomorAntrian(rs.getString("nomor_antrian"));
        p.setStatus(rs.getString("status"));
        p.setKeluhan(rs.getString("keluhan"));
        p.setCatatanAdmin(rs.getString("catatan_admin"));
        
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

        // Field join
        p.setNamaPasien(rs.getString("nama_pasien"));
        p.setNomorRm(rs.getString("nomor_rm"));
        p.setNamaDokter(rs.getString("nama_dokter"));
        p.setNamaSpesialisasi(rs.getString("nama_spesialisasi"));
        
        return p;
    }
}
