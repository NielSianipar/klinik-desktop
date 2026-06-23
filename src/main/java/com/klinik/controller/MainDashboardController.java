package com.klinik.controller;

import com.klinik.MainApp;
import com.klinik.model.Pengguna;
import com.klinik.util.AlertHelper;
import com.klinik.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Controller utama untuk mengelola MainDashboard shell.
 * Mengontrol loading sub-halaman secara dinamis berdasarkan navigasi sidebar
 * serta membatasi menu sesuai dengan role pengguna yang aktif.
 * 
 * @author Daniel sianipar
 */
public class MainDashboardController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRoleLabel;

    @FXML
    private Label viewTitleLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnOverview;

    @FXML
    private Button btnPasien;

    @FXML
    private Button btnDokter;

    @FXML
    private Button btnPendaftaran;

    @FXML
    private Button btnRekamMedis;

    @FXML
    private Button btnObat;

    @FXML
    private Button btnLaporan;

    @FXML
    private Button btnLogout;

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        // 1. Tampilkan info user login
        Pengguna user = SessionManager.getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getNamaLengkap());
            userRoleLabel.setText(user.getPeran().toUpperCase());
            applyRolePermissions(user.getPeran());
        } else {
            userNameLabel.setText("Guest");
            userRoleLabel.setText("GUEST");
        }

        // 2. Tampilkan tanggal hari ini
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", new Locale("id", "ID"));
        dateLabel.setText(today.format(formatter));

        // 3. Muat halaman Overview sebagai halaman default
        loadView("/fxml/Overview.fxml", "Ringkasan", btnOverview);
    }

    /**
     * Membatasi akses menu di sidebar berdasarkan peran (role) pengguna.
     */
    private void applyRolePermissions(String role) {
        if (role == null) return;
        
        switch (role.toLowerCase()) {
            case "dokter":
                // Dokter tidak mengurusi antrian depan, apotek, dan laporan umum
                btnPendaftaran.setVisible(false);
                btnPendaftaran.setManaged(false);
                btnObat.setVisible(false);
                btnObat.setManaged(false);
                btnLaporan.setVisible(false);
                btnLaporan.setManaged(false);
                break;
                
            case "perawat":
                // Perawat tidak mengisi rekam medis detail (dokter) dan laporan manager
                btnRekamMedis.setVisible(false);
                btnRekamMedis.setManaged(false);
                btnLaporan.setVisible(false);
                btnLaporan.setManaged(false);
                break;
                
            case "pasien":
                // Pasien hanya bisa melihat ringkasan (jadwal/antrian milik mereka)
                btnDokter.setVisible(false);
                btnDokter.setManaged(false);
                btnPendaftaran.setVisible(false);
                btnPendaftaran.setManaged(false);
                btnObat.setVisible(false);
                btnObat.setManaged(false);
                btnLaporan.setVisible(false);
                btnLaporan.setManaged(false);
                break;
                
            case "admin":
            default:
                // Admin memiliki akses ke seluruh menu
                break;
        }
    }

    /**
     * Memuat file FXML sub-layar secara dinamis ke dalam contentArea StackPane.
     */
    private void loadView(String fxmlPath, String title, Button clickedButton) {
        try {
            // Load file FXML
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            // Bersihkan area lama dan masukkan view baru
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            // Perbarui judul header
            viewTitleLabel.setText(title);

            // Kelola class CSS aktif di sidebar
            if (currentActiveButton != null) {
                currentActiveButton.getStyleClass().remove("sidebar-item-active");
            }
            clickedButton.getStyleClass().add("sidebar-item-active");
            currentActiveButton = clickedButton;

        } catch (IOException e) {
            System.err.println("Gagal memuat view: " + fxmlPath);
            e.printStackTrace();
            AlertHelper.showError("Error", "Gagal Memuat Halaman", "Halaman " + title + " tidak ditemukan atau bermasalah.");
        }
    }

    @FXML
    void showOverview(ActionEvent event) {
        loadView("/fxml/Overview.fxml", "Ringkasan", btnOverview);
    }

    @FXML
    void showPasien(ActionEvent event) {
        loadView("/fxml/PasienView.fxml", "Data Pasien", btnPasien);
    }

    @FXML
    void showDokter(ActionEvent event) {
        loadView("/fxml/DokterView.fxml", "Dokter & Jadwal Praktik", btnDokter);
    }

    @FXML
    void showPendaftaran(ActionEvent event) {
        loadView("/fxml/PendaftaranView.fxml", "Pendaftaran Antrian", btnPendaftaran);
    }

    @FXML
    void showRekamMedis(ActionEvent event) {
        loadView("/fxml/RekamMedisView.fxml", "Rekam Medis Pemeriksaan", btnRekamMedis);
    }

    @FXML
    void showObat(ActionEvent event) {
        loadView("/fxml/ObatView.fxml", "Apotek & Inventaris Obat", btnObat);
    }

    @FXML
    void showLaporan(ActionEvent event) {
        loadView("/fxml/LaporanView.fxml", "Laporan Kunjungan", btnLaporan);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        boolean confirm = AlertHelper.showConfirmation("Logout", "Konfirmasi Keluar", 
                "Apakah Anda yakin ingin keluar dari sistem? Sesi Anda akan berakhir.");
        if (confirm) {
            SessionManager.logout();
            MainApp.showLoginView();
        }
    }
}
