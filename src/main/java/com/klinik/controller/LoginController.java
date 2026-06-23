package com.klinik.controller;

import com.klinik.MainApp;
import com.klinik.dao.PenggunaDAO;
import com.klinik.model.Pengguna;
import com.klinik.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller untuk menangani interaksi pada layar Login.
 * 
 * @author Daniel sianipar
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    private final PenggunaDAO penggunaDAO = new PenggunaDAO();

    @FXML
    public void initialize() {
        // Reset state error pada saat loading
        errorLabel.setVisible(false);
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Validasi input kosong
        if (email.isEmpty() || password.isEmpty()) {
            showErrorMessage("Email dan Kata Sandi tidak boleh kosong!");
            return;
        }

        loginButton.setDisable(true);
        errorLabel.setVisible(false);

        // Lakukan autentikasi menggunakan DAO
        Pengguna user = penggunaDAO.authenticate(email, password);

        if (user != null) {
            // Simpan sesi login pengguna
            SessionManager.setCurrentUser(user);
            
            // Pindah ke layar Dashboard Utama
            MainApp.showMainView();
        } else {
            loginButton.setDisable(false);
            showErrorMessage("Email atau Kata Sandi salah, atau akun tidak aktif!");
        }
    }

    private void showErrorMessage(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
