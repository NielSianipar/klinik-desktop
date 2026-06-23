package com.klinik.dao;

import com.klinik.database.DatabaseConnection;
import com.klinik.model.Pengguna;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object untuk tabel: pengguna
 * 
 * @author Daniel sianipar
 */
public class PenggunaDAO {

    /**
     * Mencari pengguna berdasarkan email.
     * 
     * @param email Email pengguna
     * @return Objek Pengguna jika ditemukan, null jika tidak
     */
    public Pengguna findByEmail(String email) {
        String sql = "SELECT * FROM pengguna WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPengguna(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mencari pengguna berdasarkan ID.
     * 
     * @param id ID Pengguna
     * @return Objek Pengguna jika ditemukan, null jika tidak
     */
    public Pengguna findById(int id) {
        String sql = "SELECT * FROM pengguna WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPengguna(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Menyimpan pengguna baru ke database (kata sandi akan di-hash menggunakan BCrypt).
     * 
     * @param user Objek Pengguna
     * @return true jika berhasil, false jika gagal
     */
    public boolean insert(Pengguna user) {
        String sql = "INSERT INTO pengguna (nama_lengkap, email, kata_sandi, peran, aktif) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, user.getNamaLengkap());
            ps.setString(2, user.getEmail());
            
            // Hash password using BCrypt before storing
            String hashedPassword = BCrypt.hashpw(user.getKataSandi(), BCrypt.gensalt());
            ps.setString(3, hashedPassword);
            
            ps.setString(4, user.getPeran());
            ps.setBoolean(5, user.isAktif());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setIdPengguna(rs.getInt(1));
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
     * Memperbarui informasi dasar pengguna (tanpa kata sandi).
     * 
     * @param user Objek Pengguna
     * @return true jika berhasil, false jika gagal
     */
    public boolean update(Pengguna user) {
        String sql = "UPDATE pengguna SET nama_lengkap = ?, email = ?, peran = ?, aktif = ? WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getNamaLengkap());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPeran());
            ps.setBoolean(4, user.isAktif());
            ps.setInt(5, user.getIdPengguna());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Memperbarui kata sandi pengguna (akan otomatis di-hash).
     * 
     * @param id ID Pengguna
     * @param newPasswordPlain Kata sandi baru (belum di-hash)
     * @return true jika berhasil, false jika gagal
     */
    public boolean updatePassword(int id, String newPasswordPlain) {
        String sql = "UPDATE pengguna SET kata_sandi = ? WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String hashed = BCrypt.hashpw(newPasswordPlain, BCrypt.gensalt());
            ps.setString(1, hashed);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Memperbarui waktu terakhir kali pengguna login.
     * 
     * @param id ID Pengguna
     * @return true jika berhasil, false jika gagal
     */
    public boolean updateLastLogin(int id) {
        String sql = "UPDATE pengguna SET terakhir_login = ? WHERE id_pengguna = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Try fallback column 'terakhir_masuk'
            String sqlFallback = "UPDATE pengguna SET terakhir_masuk = ? WHERE id_pengguna = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sqlFallback)) {
                ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(2, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Mengambil semua daftar pengguna.
     * 
     * @return List objek Pengguna
     */
    public List<Pengguna> findAll() {
        List<Pengguna> list = new ArrayList<>();
        String sql = "SELECT * FROM pengguna";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRowToPengguna(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Autentikasi email dan kata sandi pengguna.
     * 
     * @param email Email pengguna
     * @param passwordPlain Kata sandi mentah yang diinputkan
     * @return Objek Pengguna jika berhasil login, null jika gagal
     */
    public Pengguna authenticate(String email, String passwordPlain) {
        Pengguna user = findByEmail(email);
        if (user != null && user.isAktif()) {
            if (BCrypt.checkpw(passwordPlain, user.getKataSandi())) {
                updateLastLogin(user.getIdPengguna());
                return user;
            }
        }
        return null;
    }

    private Timestamp getSafeTimestamp(ResultSet rs, String... columnNames) {
        for (String col : columnNames) {
            try {
                return rs.getTimestamp(col);
            } catch (SQLException e) {
                // Ignore and try next column name
            }
        }
        return null;
    }

    private Pengguna mapRowToPengguna(ResultSet rs) throws SQLException {
        Pengguna user = new Pengguna();
        user.setIdPengguna(rs.getInt("id_pengguna"));
        user.setNamaLengkap(rs.getString("nama_lengkap"));
        user.setEmail(rs.getString("email"));
        user.setKataSandi(rs.getString("kata_sandi"));
        user.setPeran(rs.getString("peran"));
        user.setAktif(rs.getBoolean("aktif"));
        
        Timestamp lastLogin = getSafeTimestamp(rs, "terakhir_login", "terakhir_masuk");
        if (lastLogin != null) {
            user.setTerakhirLogin(lastLogin.toLocalDateTime());
        }
        Timestamp created = getSafeTimestamp(rs, "dibuat_pada");
        if (created != null) {
            user.setDibuatPada(created.toLocalDateTime());
        }
        Timestamp updated = getSafeTimestamp(rs, "diperbarui", "diperbarui_pada");
        if (updated != null) {
            user.setDiperbarui(updated.toLocalDateTime());
        }
        return user;
    }
}
