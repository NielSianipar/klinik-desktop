package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.RekamMedis;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: rekam_medis
 * 
 * @author Daniel sianipar
 */
public class RekamMedisDAO {

    /**
     * Mencari rekam medis berdasarkan ID.
     */
    public RekamMedis findById(int id) {
        String sql = "SELECT rm.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter " +
                     "FROM rekam_medis rm " +
                     "JOIN pasien pas ON rm.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON rm.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "WHERE rm.id_rekam_medis = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRekamMedis(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari rekam medis berdasarkan ID Pendaftaran (untuk cek rekam medis per kunjungan).
     */
    public RekamMedis findByPendaftaranId(int idPendaftaran) {
        String sql = "SELECT rm.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter " +
                     "FROM rekam_medis rm " +
                     "JOIN pasien pas ON rm.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON rm.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "WHERE rm.id_pendaftaran = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPendaftaran);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRekamMedis(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mengambil riwayat rekam medis lengkap untuk seorang pasien (history).
     */
    public List<RekamMedis> findByPasienId(int idPasien) {
        List<RekamMedis> list = new ArrayList<>();
        String sql = "SELECT rm.*, pas.nama_lengkap AS nama_pasien, pas.nomor_rm, " +
                     "pe.nama_lengkap AS nama_dokter " +
                     "FROM rekam_medis rm " +
                     "JOIN pasien pas ON rm.id_pasien = pas.id_pasien " +
                     "JOIN dokter d ON rm.id_dokter = d.id_dokter " +
                     "JOIN pengguna pe ON d.id_pengguna = pe.id_pengguna " +
                     "WHERE rm.id_pasien = ? " +
                     "ORDER BY rm.tanggal_periksa DESC, rm.id_rekam_medis DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPasien);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToRekamMedis(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menambahkan rekam medis pemeriksaan baru.
     */
    public boolean insert(RekamMedis rm) {
        String sql = "INSERT INTO rekam_medis (id_pendaftaran, id_dokter, id_pasien, tanggal_periksa, tekanan_darah, suhu_tubuh, berat_badan, tinggi_badan, anamnesis, pemeriksaan_fisik, diagnosis, kode_icd, tindakan, catatan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rm.getIdPendaftaran());
            ps.setInt(2, rm.getIdDokter());
            ps.setInt(3, rm.getIdPasien());
            ps.setDate(4, Date.valueOf(rm.getTanggalPeriksa()));
            ps.setString(5, rm.getTekananDarah());
            ps.setBigDecimal(6, rm.getSuhuTubuh());
            ps.setBigDecimal(7, rm.getBeratBadan());
            ps.setBigDecimal(8, rm.getTinggiBadan());
            ps.setString(9, rm.getAnamnesis());
            ps.setString(10, rm.getPemeriksaanFisik());
            ps.setString(11, rm.getDiagnosis());
            ps.setString(12, rm.getKodeIcd());
            ps.setString(13, rm.getTindakan());
            ps.setString(14, rm.getCatatan());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        rm.setIdRekamMedis(rs.getInt(1));
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
     * Memperbarui data rekam medis.
     */
    public boolean update(RekamMedis rm) {
        String sql = "UPDATE rekam_medis SET tekanan_darah = ?, suhu_tubuh = ?, berat_badan = ?, tinggi_badan = ?, anamnesis = ?, pemeriksaan_fisik = ?, diagnosis = ?, kode_icd = ?, tindakan = ?, catatan = ? WHERE id_rekam_medis = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rm.getTekananDarah());
            ps.setBigDecimal(2, rm.getSuhuTubuh());
            ps.setBigDecimal(3, rm.getBeratBadan());
            ps.setBigDecimal(4, rm.getTinggiBadan());
            ps.setString(5, rm.getAnamnesis());
            ps.setString(6, rm.getPemeriksaanFisik());
            ps.setString(7, rm.getDiagnosis());
            ps.setString(8, rm.getKodeIcd());
            ps.setString(9, rm.getTindakan());
            ps.setString(10, rm.getCatatan());
            ps.setInt(11, rm.getIdRekamMedis());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private RekamMedis mapRowToRekamMedis(ResultSet rs) throws SQLException {
        RekamMedis rm = new RekamMedis();
        rm.setIdRekamMedis(rs.getInt("id_rekam_medis"));
        rm.setIdPendaftaran(rs.getInt("id_pendaftaran"));
        rm.setIdDokter(rs.getInt("id_dokter"));
        rm.setIdPasien(rs.getInt("id_pasien"));
        
        Date examDate = rs.getDate("tanggal_periksa");
        if (examDate != null) {
            rm.setTanggalPeriksa(examDate.toLocalDate());
        }
        
        rm.setTekananDarah(rs.getString("tekanan_darah"));
        rm.setSuhuTubuh(rs.getBigDecimal("suhu_tubuh"));
        rm.setBeratBadan(rs.getBigDecimal("berat_badan"));
        rm.setTinggiBadan(rs.getBigDecimal("tinggi_badan"));
        rm.setAnamnesis(rs.getString("anamnesis"));
        rm.setPemeriksaanFisik(rs.getString("pemeriksaan_fisik"));
        rm.setDiagnosis(rs.getString("diagnosis"));
        rm.setKodeIcd(rs.getString("kode_icd"));
        rm.setTindakan(rs.getString("tindakan"));
        rm.setCatatan(rs.getString("catatan"));
        
        Timestamp created = rs.getTimestamp("dibuat_pada");
        if (created != null) {
            rm.setDibuatPada(created.toLocalDateTime());
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
            rm.setDiperbarui(updated.toLocalDateTime());
        }

        // Field join
        rm.setNamaPasien(rs.getString("nama_pasien"));
        rm.setNomorRm(rs.getString("nomor_rm"));
        rm.setNamaDokter(rs.getString("nama_dokter"));
        
        return rm;
    }
}
