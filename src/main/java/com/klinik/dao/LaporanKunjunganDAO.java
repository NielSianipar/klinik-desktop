package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.LaporanKunjungan;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: laporan_kunjungan
 * 
 * @author Daniel sianipar
 */
public class LaporanKunjunganDAO {

    /**
     * Mengambil semua riwayat laporan kunjungan yang telah disimpan.
     */
    public List<LaporanKunjungan> findAll() {
        List<LaporanKunjungan> list = new ArrayList<>();
        String sql = "SELECT l.*, pd.nama_lengkap AS nama_dokter, s.nama_spesialisasi, pu.nama_lengkap AS nama_pembuat " +
                     "FROM laporan_kunjungan l " +
                     "JOIN dokter d ON l.id_dokter = d.id_dokter " +
                     "JOIN pengguna pd ON d.id_pengguna = pd.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "LEFT JOIN pengguna pu ON l.dibuat_oleh = pu.id_pengguna " +
                     "ORDER BY l.dibuat_pada DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToLaporan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menghitung statistik kunjungan secara realtime untuk periode tertentu
     * sebelum disimpan sebagai laporan formal.
     * 
     * @param idDokter ID Dokter
     * @param mulai Tanggal awal periode
     * @param selesai Tanggal akhir periode
     * @return LaporanKunjungan berisi perhitungan statistik kunjungan
     */
    public LaporanKunjungan hitungStatistik(int idDokter, LocalDate mulai, LocalDate selesai) {
        LaporanKunjungan lap = new LaporanKunjungan();
        lap.setIdDokter(idDokter);
        lap.setPeriodeMulai(mulai);
        lap.setPeriodeSelesai(selesai);

        // 1. Hitung total kunjungan yang selesai
        String sqlTotalKunjungan = "SELECT COUNT(*) FROM pendaftaran " +
                                   "WHERE id_dokter = ? AND tanggal_kunjungan BETWEEN ? AND ? AND status = 'selesai'";
        
        // 2. Hitung total pasien baru (kunjungan pertama kali) pada periode ini
        String sqlPasienBaru = "SELECT COUNT(DISTINCT id_pasien) FROM pendaftaran " +
                               "WHERE id_dokter = ? AND tanggal_kunjungan BETWEEN ? AND ? AND status = 'selesai' " +
                               "AND id_pasien NOT IN (" +
                               "  SELECT id_pasien FROM pendaftaran WHERE tanggal_kunjungan < ?" +
                               ")";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Hitung Total Kunjungan
            try (PreparedStatement ps = conn.prepareStatement(sqlTotalKunjungan)) {
                ps.setInt(1, idDokter);
                ps.setDate(2, Date.valueOf(mulai));
                ps.setDate(3, Date.valueOf(selesai));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lap.setTotalKunjungan(rs.getInt(1));
                    }
                }
            }

            // Hitung Pasien Baru
            try (PreparedStatement ps = conn.prepareStatement(sqlPasienBaru)) {
                ps.setInt(1, idDokter);
                ps.setDate(2, Date.valueOf(mulai));
                ps.setDate(3, Date.valueOf(selesai));
                ps.setDate(4, Date.valueOf(mulai));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        lap.setTotalPasienBaru(rs.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lap;
    }

    /**
     * Menyimpan laporan kunjungan baru.
     */
    public boolean insert(LaporanKunjungan l) {
        String sql = "INSERT INTO laporan_kunjungan (id_dokter, periode_mulai, periode_selesai, total_kunjungan, total_pasien_baru, keterangan, dibuat_oleh) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, l.getIdDokter());
            ps.setDate(2, Date.valueOf(l.getPeriodeMulai()));
            ps.setDate(3, Date.valueOf(l.getPeriodeSelesai()));
            ps.setInt(4, l.getTotalKunjungan());
            ps.setInt(5, l.getTotalPasienBaru());
            ps.setString(6, l.getKeterangan());
            
            if (l.getDibuatOleh() != null) {
                ps.setInt(7, l.getDibuatOleh());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        l.setIdLaporan(rs.getInt(1));
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
     * Menghapus riwayat laporan kunjungan.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM laporan_kunjungan WHERE id_laporan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private LaporanKunjungan mapRowToLaporan(ResultSet rs) throws SQLException {
        LaporanKunjungan l = new LaporanKunjungan();
        l.setIdLaporan(rs.getInt("id_laporan"));
        l.setIdDokter(rs.getInt("id_dokter"));
        
        Date startDate = rs.getDate("periode_mulai");
        if (startDate != null) {
            l.setPeriodeMulai(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("periode_selesai");
        if (endDate != null) {
            l.setPeriodeSelesai(endDate.toLocalDate());
        }
        
        l.setTotalKunjungan(rs.getInt("total_kunjungan"));
        l.setTotalPasienBaru(rs.getInt("total_pasien_baru"));
        l.setKeterangan(rs.getString("keterangan"));
        
        int creatorVal = rs.getInt("dibuat_oleh");
        l.setDibuatOleh(rs.wasNull() ? null : creatorVal);
        
        Timestamp created = rs.getTimestamp("dibuat_pada");
        if (created != null) {
            l.setDibuatPada(created.toLocalDateTime());
        }

        // Field join
        l.setNamaDokter(rs.getString("nama_dokter"));
        l.setNamaSpesialisasi(rs.getString("nama_spesialisasi"));
        l.setNamaPembuat(rs.getString("nama_pembuat"));
        
        return l;
    }
}
