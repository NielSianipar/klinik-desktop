package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.Dokter;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: dokter
 * 
 * @author Daniel sianipar
 */
public class DokterDAO {

    /**
     * Mengambil semua daftar dokter lengkap dengan nama lengkap dan nama spesialisasi.
     */
    public List<Dokter> findAll() {
        List<Dokter> list = new ArrayList<>();
        String sql = "SELECT d.*, p.nama_lengkap, s.nama_spesialisasi " +
                     "FROM dokter d " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "ORDER BY p.nama_lengkap ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToDokter(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari dokter berdasarkan ID dokter.
     */
    public Dokter findById(int id) {
        String sql = "SELECT d.*, p.nama_lengkap, s.nama_spesialisasi " +
                     "FROM dokter d " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE d.id_dokter = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDokter(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari dokter berdasarkan ID pengguna.
     */
    public Dokter findByPenggunaId(int idPengguna) {
        String sql = "SELECT d.*, p.nama_lengkap, s.nama_spesialisasi " +
                     "FROM dokter d " +
                     "JOIN pengguna p ON d.id_pengguna = p.id_pengguna " +
                     "JOIN spesialisasi s ON d.id_spesialisasi = s.id_spesialisasi " +
                     "WHERE d.id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPengguna);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDokter(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Menambahkan profil dokter baru.
     */
    public boolean insert(Dokter d) {
        String sql = "INSERT INTO dokter (id_pengguna, id_spesialisasi, nomor_sip, telepon, bio, foto) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, d.getIdPengguna());
            ps.setInt(2, d.getIdSpesialisasi());
            ps.setString(3, d.getNomorSip());
            ps.setString(4, d.getTelepon());
            ps.setString(5, d.getBio());
            ps.setString(6, d.getFoto());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        d.setIdDokter(rs.getInt(1));
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
     * Memperbarui profil dokter.
     */
    public boolean update(Dokter d) {
        String sql = "UPDATE dokter SET id_spesialisasi = ?, nomor_sip = ?, telepon = ?, bio = ?, foto = ? WHERE id_dokter = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getIdSpesialisasi());
            ps.setString(2, d.getNomorSip());
            ps.setString(3, d.getTelepon());
            ps.setString(4, d.getBio());
            ps.setString(5, d.getFoto());
            ps.setInt(6, d.getIdDokter());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menghapus dokter berdasarkan ID dokter.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM dokter WHERE id_dokter = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Dokter mapRowToDokter(ResultSet rs) throws SQLException {
        Dokter d = new Dokter();
        d.setIdDokter(rs.getInt("id_dokter"));
        d.setIdPengguna(rs.getInt("id_pengguna"));
        d.setIdSpesialisasi(rs.getInt("id_spesialisasi"));
        d.setNomorSip(rs.getString("nomor_sip"));
        d.setTelepon(rs.getString("telepon"));
        d.setBio(rs.getString("bio"));
        d.setFoto(rs.getString("foto"));
        
        Timestamp created = rs.getTimestamp("dibuat_pada");
        if (created != null) {
            d.setDibuatPada(created.toLocalDateTime());
        }

        // Field join
        d.setNamaLengkap(rs.getString("nama_lengkap"));
        d.setNamaSpesialisasi(rs.getString("nama_spesialisasi"));
        
        return d;
    }
}
