package com.klinik;

import java.sql.*;

public class CheckAllPasien {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/db_klinik?useSSL=false&serverTimezone=Asia/Jakarta&characterEncoding=UTF-8";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("\n--- All pasien rows ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM pasien")) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                while (rs.next()) {
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(metaData.getColumnName(i) + ": " + rs.getObject(i) + " | ");
                    }
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
