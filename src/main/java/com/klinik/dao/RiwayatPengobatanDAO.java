package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.RiwayatPengobatan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: riwayat_pengobatan (detail resep obat per rekam medis)
 * 
 * @author Daniel sianipar
 */
public class RiwayatPengobatanDAO {

    /**
     * Mengambil daftar obat yang diresepkan untuk satu rekam medis tertentu.
     */
    public List<RiwayatPengobatan> findByRekamMedisId(int idRekamMedis) {
        List<RiwayatPengobatan> list = new ArrayList<>();
        String sql = "SELECT rp.*, o.nama_obat, o.satuan, o.kode_obat " +
                     "FROM riwayat_pengobatan rp " +
                     "JOIN obat o ON rp.id_obat = o.id_obat " +
                     "WHERE rp.id_rekam_medis = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRekamMedis);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RiwayatPengobatan rp = new RiwayatPengobatan();
                    rp.setIdRiwayat(rs.getInt("id_riwayat"));
                    rp.setIdRekamMedis(rs.getInt("id_rekam_medis"));
                    rp.setIdObat(rs.getInt("id_obat"));
                    rp.setJumlah(rs.getInt("jumlah"));
                    rp.setAturanPakai(rs.getString("aturan_pakai"));
                    rp.setCatatanApoteker(rs.getString("catatan_apoteker"));
                    
                    Timestamp created = rs.getTimestamp("dibuat_pada");
                    if (created != null) {
                        rp.setDibuatPada(created.toLocalDateTime());
                    }

                    // Field join
                    rp.setNamaObat(rs.getString("nama_obat"));
                    rp.setSatuan(rs.getString("satuan"));
                    rp.setKodeObat(rs.getString("kode_obat"));

                    list.add(rp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menambahkan detail resep obat baru.
     */
    public boolean insert(RiwayatPengobatan rp) {
        String sql = "INSERT INTO riwayat_pengobatan (id_rekam_medis, id_obat, jumlah, aturan_pakai, catatan_apoteker) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rp.getIdRekamMedis());
            ps.setInt(2, rp.getIdObat());
            ps.setInt(3, rp.getJumlah());
            ps.setString(4, rp.getAturanPakai());
            ps.setString(5, rp.getCatatanApoteker());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rp.setIdRiwayat(rs.getInt(1));
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
     * Memperbarui catatan apoteker untuk resep obat (misal saat obat diserahkan).
     */
    public boolean updateCatatanApoteker(int idRiwayat, String catatan) {
        String sql = "UPDATE riwayat_pengobatan SET catatan_apoteker = ? WHERE id_riwayat = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, catatan);
            ps.setInt(2, idRiwayat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menghapus semua detail resep untuk rekam medis tertentu (berguna jika dokter merevisi resep).
     */
    public boolean deleteByRekamMedisId(int idRekamMedis) {
        String sql = "DELETE FROM riwayat_pengobatan WHERE id_rekam_medis = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRekamMedis);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
