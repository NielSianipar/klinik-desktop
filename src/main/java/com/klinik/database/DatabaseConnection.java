package com.klinik.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Helper untuk menangani koneksi ke database MySQL.
 * Membaca konfigurasi dari file database.properties.
 * 
 * @author Daniel sianipar
 */
public class DatabaseConnection {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConnection.class.getResourceAsStream("/database.properties")) {
            if (input == null) {
                System.err.println("Error: file database.properties tidak ditemukan di classpath.");
            } else {
                properties.load(input);
                // Load MySQL Driver class explicitly
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Gagal menginisialisasi driver database atau file properties.");
            e.printStackTrace();
        }
    }

    /**
     * Membuka koneksi baru ke database.
     * 
     * @return Connection ke database MySQL
     * @throws SQLException jika terjadi kesalahan saat koneksi
     */
    public static Connection getConnection() throws SQLException {
        String host = properties.getProperty("db.host", "localhost");
        String port = properties.getProperty("db.port", "3306");
        String name = properties.getProperty("db.name", "db_klinik");
        String username = properties.getProperty("db.username", "root");
        String password = properties.getProperty("db.password", "");
        String charset = properties.getProperty("db.charset", "utf8mb4");

        // Bentuk URL koneksi JDBC
        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=Asia/Jakarta&characterEncoding=%s",
                host, port, name, charset);

        return DriverManager.getConnection(url, username, password);
    }
}
