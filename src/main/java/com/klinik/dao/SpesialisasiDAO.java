package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.Spesialisasi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: spesialisasi
 * 
 * @author Daniel sianipar
 */
public class SpesialisasiDAO {

    /**
     * Mengambil semua data spesialisasi dokter.
     */
    public List<Spesialisasi> findAll() {
        List<Spesialisasi> list = new ArrayList<>();
        String sql = "SELECT * FROM spesialisasi ORDER BY nama_spesialisasi ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Spesialisasi s = new Spesialisasi();
                s.setIdSpesialisasi(rs.getInt("id_spesialisasi"));
                s.setNamaSpesialisasi(rs.getString("nama_spesialisasi"));
                s.setKeterangan(rs.getString("keterangan"));
                list.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Mencari spesialisasi berdasarkan ID.
     */
    public Spesialisasi findById(int id) {
        String sql = "SELECT * FROM spesialisasi WHERE id_spesialisasi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Spesialisasi s = new Spesialisasi();
                    s.setIdSpesialisasi(rs.getInt("id_spesialisasi"));
                    s.setNamaSpesialisasi(rs.getString("nama_spesialisasi"));
                    s.setKeterangan(rs.getString("keterangan"));
                    return s;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
