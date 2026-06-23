package com.klinik.controller;

import com.klinik.dao.PasienDAO;
import com.klinik.model.Pasien;
import com.klinik.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller untuk mengelola interaksi CRUD Pasien pada PasienView.
 * 
 * @author Daniel sianipar
 */
public class PasienController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Pasien> pasienTableView;

    @FXML
    private TableColumn<Pasien, String> colRm;

    @FXML
    private TableColumn<Pasien, String> colNama;

    @FXML
    private TableColumn<Pasien, String> colTelepon;

    @FXML
    private TableColumn<Pasien, Integer> colUsia;

    // Form fields
    @FXML
    private TextField rmField;

    @FXML
    private TextField namaField;

    @FXML
    private DatePicker dobDatePicker;

    @FXML
    private ComboBox<String> genderComboBox;

    @FXML
    private TextField teleponField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> bloodComboBox;

    @FXML
    private TextArea alergiArea;

    @FXML
    private TextArea alamatArea;

    private final PasienDAO pasienDAO = new PasienDAO();
    private Pasien selectedPasien = null;

    @FXML
    public void initialize() {
        // 1. Konfigurasi kolom tabel
        colRm.setCellValueFactory(new PropertyValueFactory<>("nomorRm"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        colTelepon.setCellValueFactory(new PropertyValueFactory<>("telepon"));
        colUsia.setCellValueFactory(new PropertyValueFactory<>("usia"));

        // 2. Set item ComboBox
        genderComboBox.setItems(FXCollections.observableArrayList("Laki-laki", "Perempuan"));
        bloodComboBox.setItems(FXCollections.observableArrayList("A", "B", "AB", "O"));

        // 3. Listener pilihan baris tabel
        pasienTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPasien = newVal;
                showPasienDetails(newVal);
            }
        });

        // 4. Load data awal
        loadPasienData();
        clearForm();
    }

    private void loadPasienData() {
        List<Pasien> list = pasienDAO.findAll();
        pasienTableView.setItems(FXCollections.observableArrayList(list));
    }

    private void showPasienDetails(Pasien p) {
        rmField.setText(p.getNomorRm());
        namaField.setText(p.getNamaLengkap());
        dobDatePicker.setValue(p.getTanggalLahir());
        genderComboBox.setValue(p.getJenisKelamin());
        teleponField.setText(p.getTelepon());
        emailField.setText(p.getEmail());
        bloodComboBox.setValue(p.getGolonganDarah());
        alergiArea.setText(p.getRiwayatAlergi());
        alamatArea.setText(p.getAlamat());
    }

    private void clearForm() {
        selectedPasien = null;
        rmField.setText(pasienDAO.generateNomorRm()); // Otomatis generate RM baru
        namaField.setText("");
        dobDatePicker.setValue(null);
        genderComboBox.setValue(null);
        teleponField.setText("");
        emailField.setText("");
        bloodComboBox.setValue(null);
        alergiArea.setText("");
        alamatArea.setText("");
        pasienTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadPasienData();
        } else {
            List<Pasien> list = pasienDAO.search(query);
            pasienTableView.setItems(FXCollections.observableArrayList(list));
        }
    }

    @FXML
    void handleNew(ActionEvent event) {
        clearForm();
    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selectedPasien == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Pasien", "Silakan pilih pasien yang ingin dihapus dari tabel terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Hapus Pasien", "Konfirmasi Hapus", 
                "Apakah Anda yakin ingin menghapus data pasien " + selectedPasien.getNamaLengkap() + "?");
        
        if (confirm) {
            boolean success = pasienDAO.delete(selectedPasien.getIdPasien());
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Dihapus", "Pasien berhasil dihapus.");
                loadPasienData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menghapus", "Terjadi kesalahan saat menghapus data pasien.");
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String nama = namaField.getText().trim();
        String rm = rmField.getText().trim();

        // Validasi field wajib
        if (nama.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Nama Kosong", "Nama Lengkap pasien harus diisi!");
            return;
        }

        if (rm.isEmpty() || rm.equals("[ Otomatis ]")) {
            rm = pasienDAO.generateNomorRm();
        }

        // Ambil nilai dari form
        LocalDate dob = dobDatePicker.getValue();
        String gender = genderComboBox.getValue();
        String telepon = teleponField.getText().trim();
        String email = emailField.getText().trim();
        String golDarah = bloodComboBox.getValue();
        String alergi = alergiArea.getText().trim();
        String alamat = alamatArea.getText().trim();

        if (selectedPasien == null) {
            // Tambah Baru (INSERT)
            Pasien p = new Pasien(rm, nama, dob, gender, telepon);
            p.setEmail(email);
            p.setGolonganDarah(golDarah);
            p.setRiwayatAlergi(alergi);
            p.setAlamat(alamat);

            boolean success = pasienDAO.insert(p);
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Disimpan", "Pasien baru berhasil didaftarkan.");
                loadPasienData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal menyimpan pasien baru. Pastikan nomor RM belum terduplikasi.");
            }
        } else {
            // Edit Data (UPDATE)
            selectedPasien.setNamaLengkap(nama);
            selectedPasien.setTanggalLahir(dob);
            selectedPasien.setJenisKelamin(gender);
            selectedPasien.setTelepon(telepon);
            selectedPasien.setEmail(email);
            selectedPasien.setGolonganDarah(golDarah);
            selectedPasien.setRiwayatAlergi(alergi);
            selectedPasien.setAlamat(alamat);

            boolean success = pasienDAO.update(selectedPasien);
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Diperbarui", "Data pasien berhasil diperbarui.");
                loadPasienData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Terjadi kesalahan saat memperbarui data pasien.");
            }
        }
    }
}
