package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.Obat;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: obat
 * 
 * @author Daniel sianipar
 */
public class ObatDAO {

    /**
     * Mencari obat berdasarkan ID.
     */
    public Obat findById(int id) {
        String sql = "SELECT * FROM obat WHERE id_obat = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToObat(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari obat berdasarkan kode obat.
     */
    public Obat findByKode(String kodeObat) {
        String sql = "SELECT * FROM obat WHERE kode_obat = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kodeObat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToObat(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mengambil semua daftar obat.
     */
    public List<Obat> findAll() {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat ORDER BY nama_obat ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToObat(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari obat secara dinamis berdasarkan nama, kode, atau kategori.
     */
    public List<Obat> search(String query) {
        List<Obat> list = new ArrayList<>();
        String sql = "SELECT * FROM obat WHERE nama_obat LIKE ? OR kode_obat LIKE ? OR kategori LIKE ? ORDER BY nama_obat ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String keyword = "%" + query + "%";
            ps.setString(1, keyword);
            ps.setString(2, keyword);
            ps.setString(3, keyword);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToObat(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Menambahkan obat baru.
     */
    public boolean insert(Obat o) {
        String sql = "INSERT INTO obat (kode_obat, nama_obat, kategori, satuan, stok, harga, keterangan, aktif) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getKodeObat());
            ps.setString(2, o.getNamaObat());
            ps.setString(3, o.getKategori());
            ps.setString(4, o.getSatuan());
            ps.setInt(5, o.getStok());
            ps.setBigDecimal(6, o.getHarga());
            ps.setString(7, o.getKeterangan());
            ps.setBoolean(8, o.isAktif());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        o.setIdObat(rs.getInt(1));
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
     * Memperbarui data obat.
     */
    public boolean update(Obat o) {
        String sql = "UPDATE obat SET kode_obat = ?, nama_obat = ?, kategori = ?, satuan = ?, stok = ?, harga = ?, keterangan = ?, aktif = ? WHERE id_obat = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, o.getKodeObat());
            ps.setString(2, o.getNamaObat());
            ps.setString(3, o.getKategori());
            ps.setString(4, o.getSatuan());
            ps.setInt(5, o.getStok());
            ps.setBigDecimal(6, o.getHarga());
            ps.setString(7, o.getKeterangan());
            ps.setBoolean(8, o.isAktif());
            ps.setInt(9, o.getIdObat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Memperbarui stok obat (bisa bertambah atau berkurang).
     * Berguna saat apoteker memberikan resep atau saat re-stock.
     * 
     * @param id ID Obat
     * @param delta Perubahan jumlah stok (positif untuk tambah, negatif untuk kurang)
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateStok(int id, int delta) {
        String sql = "UPDATE obat SET stok = stok + ? WHERE id_obat = ? AND (stok + ?) >= 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, id);
            ps.setInt(3, delta); // memastikan stok akhir tidak negatif
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menonaktifkan obat (soft delete).
     */
    public boolean deactivate(int id) {
        String sql = "UPDATE obat SET aktif = 0 WHERE id_obat = ?";
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
     * Menghapus obat secara permanen dari database.
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM obat WHERE id_obat = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Obat mapRowToObat(ResultSet rs) throws SQLException {
        Obat o = new Obat();
        o.setIdObat(rs.getInt("id_obat"));
        o.setKodeObat(rs.getString("kode_obat"));
        o.setNamaObat(rs.getString("nama_obat"));
        o.setKategori(rs.getString("kategori"));
        o.setSatuan(rs.getString("satuan"));
        o.setStok(rs.getInt("stok"));
        o.setHarga(rs.getBigDecimal("harga"));
        o.setKeterangan(rs.getString("keterangan"));
        o.setAktif(rs.getBoolean("aktif"));
        
        Timestamp created = rs.getTimestamp("dibuat_pada");
        if (created != null) {
            o.setDibuatPada(created.toLocalDateTime());
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
            o.setDiperbarui(updated.toLocalDateTime());
        }
        return o;
    }
}
