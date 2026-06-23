package com.klinik.controller;

import com.klinik.dao.DokterDAO;
import com.klinik.dao.ObatDAO;
import com.klinik.dao.PasienDAO;
import com.klinik.dao.PendaftaranDAO;
import com.klinik.model.Obat;
import com.klinik.model.Pendaftaran;
import com.klinik.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller untuk mengelola data di sub-layar Ringkasan (Overview).
 * 
 * @author Daniel sianipar
 */
public class OverviewController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label totalPasienLabel;

    @FXML
    private Label totalAntrianLabel;

    @FXML
    private Label totalObatMenipisLabel;

    @FXML
    private Label totalDokterLabel;

    @FXML
    private TableView<Pendaftaran> queueTableView;

    @FXML
    private TableColumn<Pendaftaran, String> colNoAntrian;

    @FXML
    private TableColumn<Pendaftaran, String> colPasien;

    @FXML
    private TableColumn<Pendaftaran, String> colDokter;

    @FXML
    private TableColumn<Pendaftaran, String> colStatus;

    // DAOs
    private final PasienDAO pasienDAO = new PasienDAO();
    private final PendaftaranDAO pendaftaranDAO = new PendaftaranDAO();
    private final ObatDAO obatDAO = new ObatDAO();
    private final DokterDAO dokterDAO = new DokterDAO();

    @FXML
    public void initialize() {
        // 1. Tampilkan ucapan selamat datang sesuai user aktif
        if (SessionManager.isLoggedIn()) {
            welcomeLabel.setText("Selamat Datang, " + SessionManager.getCurrentUser().getNamaLengkap() + "!");
        }

        // 2. Load data statistik dari database
        loadStatistics();

        // 3. Konfigurasi kolom tabel antrian
        colNoAntrian.setCellValueFactory(new PropertyValueFactory<>("nomorAntrian"));
        colPasien.setCellValueFactory(new PropertyValueFactory<>("namaPasien"));
        colDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 4. Load data tabel antrian hari ini
        loadTodayQueue();
    }

    private void loadStatistics() {
        // Hitung total pasien terdaftar
        int totalPasien = pasienDAO.findAll().size();
        totalPasienLabel.setText(String.valueOf(totalPasien));

        // Hitung antrian hari ini
        int totalAntrian = pendaftaranDAO.findByDate(LocalDate.now()).size();
        totalAntrianLabel.setText(String.valueOf(totalAntrian));

        // Hitung obat yang stoknya menipis (< 10)
        List<Obat> allObat = obatDAO.findAll();
        long lowStockCount = allObat.stream()
                .filter(Obat::isAktif)
                .filter(o -> o.getStok() < 10)
                .count();
        totalObatMenipisLabel.setText(String.valueOf(lowStockCount));

        // Hitung dokter yang aktif
        int totalDokter = dokterDAO.findAll().size();
        totalDokterLabel.setText(String.valueOf(totalDokter));
    }

    private void loadTodayQueue() {
        List<Pendaftaran> todayQueues = pendaftaranDAO.findByDate(LocalDate.now());
        
        // Jika user adalah DOKTER, filter hanya tampilkan antrian miliknya saja
        if (SessionManager.isLoggedIn() && "dokter".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran())) {
            int idPenggunaDokter = SessionManager.getCurrentUser().getIdPengguna();
            todayQueues = todayQueues.stream()
                    .filter(p -> {
                        // Cari detail dokter berdasarkan idPengguna
                        com.klinik.model.Dokter d = dokterDAO.findByPenggunaId(idPenggunaDokter);
                        return d != null && p.getIdDokter() == d.getIdDokter();
                    })
                    .collect(Collectors.toList());
        }

        queueTableView.setItems(FXCollections.observableArrayList(todayQueues));
    }
}
