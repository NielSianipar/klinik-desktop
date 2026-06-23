package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.JadwalDokter;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: jadwal_dokter
 * 
 * @author Daniel sianipar
 */
public class JadwalDokterDAO {

    /**
     * Mengambil semua jadwal dokter beserta info nama dokter dan spesialisasi.
     */
    public List<JadwalDokter> findAll() {
        List<JadwalDokter> list = new ArrayList<>();
        String sql = "SELECT jd.*, p.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM jadwal_dokter jd " +
                     "JOIN dokter d ON jd.id_dokter = d.id_dokter " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "ORDER BY FIELD(jd.hari, 'senin', 'selasa', 'rabu', 'kamis', 'jumat', 'sabtu', 'minggu'), jd.jam_mulai ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToJadwal(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari jadwal berdasarkan ID jadwal.
     */
    public JadwalDokter findById(int id) {
        String sql = "SELECT jd.*, p.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM jadwal_dokter jd " +
                     "JOIN dokter d ON jd.id_dokter = d.id_dokter " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE jd.id_jadwal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToJadwal(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari semua jadwal untuk dokter tertentu.
     */
    public List<JadwalDokter> findByDokterId(int idDokter) {
        List<JadwalDokter> list = new ArrayList<>();
        String sql = "SELECT jd.*, p.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM jadwal_dokter jd " +
                     "JOIN dokter d ON jd.id_dokter = d.id_dokter " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE jd.id_dokter = ? " +
                     "ORDER BY FIELD(jd.hari, 'senin', 'selasa', 'rabu', 'kamis', 'jumat', 'sabtu', 'minggu'), jd.jam_mulai ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDokter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToJadwal(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari jadwal berdasarkan hari pelayanan.
     */
    public List<JadwalDokter> findByHari(String hari) {
        List<JadwalDokter> list = new ArrayList<>();
        String sql = "SELECT jd.*, p.nama_lengkap AS nama_dokter, s.nama_spesialisasi " +
                     "FROM jadwal_dokter jd " +
                     "JOIN dokter d ON jd.id_dokter = d.id_dokter " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE jd.hari = ? AND jd.tersedia = 1 " +
                     "ORDER BY jd.jam_mulai ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hari.toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToJadwal(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menambahkan jadwal dokter baru.
     */
    public boolean insert(JadwalDokter jd) {
        String sql = "INSERT INTO jadwal_dokter (id_dokter, hari, jam_mulai, jam_selesai, kuota, tersedia) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, jd.getIdDokter());
            ps.setString(2, jd.getHari().toLowerCase());
            ps.setTime(3, Time.valueOf(jd.getJamMulai()));
            ps.setTime(4, Time.valueOf(jd.getJamSelesai()));
            ps.setInt(5, jd.getKuota());
            ps.setBoolean(6, jd.isTersedia());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        jd.setIdJadwal(rs.getInt(1));
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
     * Memperbarui jadwal dokter.
     */
    public boolean update(JadwalDokter jd) {
        String sql = "UPDATE jadwal_dokter SET id_dokter = ?, hari = ?, jam_mulai = ?, jam_selesai = ?, kuota = ?, tersedia = ? WHERE id_jadwal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, jd.getIdDokter());
            ps.setString(2, jd.getHari().toLowerCase());
            ps.setTime(3, Time.valueOf(jd.getJamMulai()));
            ps.setTime(4, Time.valueOf(jd.getJamSelesai()));
            ps.setInt(5, jd.getKuota());
            ps.setBoolean(6, jd.isTersedia());
            ps.setInt(7, jd.getIdJadwal());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menghapus jadwal dokter secara permanen.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM jadwal_dokter WHERE id_jadwal = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private JadwalDokter mapRowToJadwal(ResultSet rs) throws SQLException {
        JadwalDokter jd = new JadwalDokter();
        jd.setIdJadwal(rs.getInt("id_jadwal"));
        jd.setIdDokter(rs.getInt("id_dokter"));
        jd.setHari(rs.getString("hari"));
        
        Time startTime = rs.getTime("jam_mulai");
        if (startTime != null) {
            jd.setJamMulai(startTime.toLocalTime());
        }
        Time endTime = rs.getTime("jam_selesai");
        if (endTime != null) {
            jd.setJamSelesai(endTime.toLocalTime());
        }
        
        jd.setKuota(rs.getInt("kuota"));
        jd.setTersedia(rs.getBoolean("tersedia"));

        // Field join
        jd.setNamaDokter(rs.getString("nama_dokter"));
        jd.setNamaSpesialisasi(rs.getString("nama_spesialisasi"));
        
        return jd;
    }
}
