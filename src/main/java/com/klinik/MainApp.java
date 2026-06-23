package com.klinik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.text.Font;
import java.io.IOException;

/**
 * Main Class Entry Point untuk Aplikasi Desktop JavaFX Sistem Manajemen Klinik.
 * Mengelola navigasi Window/Stage utama.
 * 
 * @author Daniel sianipar
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Load custom Google Fonts
        try {
            Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Poppins-Regular.ttf"), 12);
            Font.loadFont(MainApp.class.getResourceAsStream("/fonts/Poppins-Bold.ttf"), 12);
        } catch (Exception e) {
            System.err.println("Gagal memuat custom font Poppins, menggunakan fallback.");
            e.printStackTrace();
        }
        
        primaryStage = stage;
        showLoginView();
    }

    /**
     * Menampilkan jendela Login.
     */
    public static void showLoginView() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(root);
            
            // Tambahkan stylesheet global
            String cssPath = MainApp.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            primaryStage.setTitle("Sistem Manajemen Klinik - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman login (Login.fxml).");
            e.printStackTrace();
        }
    }

    /**
     * Menampilkan jendela Dashboard Utama setelah login berhasil.
     */
    public static void showMainView() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("/fxml/MainDashboard.fxml"));
            Scene scene = new Scene(root);
            
            // Tambahkan stylesheet global
            String cssPath = MainApp.class.getResource("/css/style.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            primaryStage.setTitle("Sistem Manajemen Klinik - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman dashboard utama (MainDashboard.fxml).");
            e.printStackTrace();
        }
    }

    /**
     * Mendapatkan Stage utama.
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
