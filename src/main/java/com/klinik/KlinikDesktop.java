package com.klinik;

import com.klinik.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class KlinikDesktop {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("--- Pasien ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id_pasien, nama_lengkap FROM pasien LIMIT 1")) {
                if (rs.next()) {
                    System.out.println("ID Pasien: " + rs.getInt("id_pasien") + ", Nama: " + rs.getString("nama_lengkap"));
                }
            }
            System.out.println("--- Dokter ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id_dokter FROM dokter LIMIT 1")) {
                if (rs.next()) {
                    System.out.println("ID Dokter: " + rs.getInt("id_dokter"));
                }
            }
            System.out.println("--- Jadwal ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id_jadwal FROM jadwal_dokter LIMIT 1")) {
                if (rs.next()) {
                    System.out.println("ID Jadwal: " + rs.getInt("id_jadwal"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
