package com.klinik.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Utility untuk memudahkan penampilan dialog Alert/Notifikasi di JavaFX.
 * 
 * @author Daniel sianipar
 */
public class AlertHelper {

    /**
     * Menampilkan alert informasi.
     */
    public static void showInfo(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    /**
     * Menampilkan alert error.
     */
    public static void showError(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    /**
     * Menampilkan alert peringatan.
     */
    public static void showWarning(String title, String header, String content) {
        showAlert(AlertType.WARNING, title, header, content);
    }

    private static void showAlert(AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Menampilkan konfirmasi (Ya/Tidak).
     * 
     * @return true jika user menekan tombol OK, false jika cancel/close
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
