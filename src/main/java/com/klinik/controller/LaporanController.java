package com.klinik.controller;

import com.klinik.dao.DokterDAO;
import com.klinik.dao.LaporanKunjunganDAO;
import com.klinik.model.Dokter;
import com.klinik.model.LaporanKunjungan;
import com.klinik.util.AlertHelper;
import com.klinik.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller untuk mengelola pembuatan laporan kunjungan berkala.
 * 
 * @author Daniel sianipar
 */
public class LaporanController {

    @FXML
    private TableView<LaporanKunjungan> laporanTableView;

    @FXML
    private TableColumn<LaporanKunjungan, String> colDokter;

    @FXML
    private TableColumn<LaporanKunjungan, String> colPeriode;

    @FXML
    private TableColumn<LaporanKunjungan, Integer> colKunjungan;

    @FXML
    private TableColumn<LaporanKunjungan, Integer> colPasienBaru;

    // Form fields
    @FXML
    private ComboBox<Dokter> dokterComboBox;

    @FXML
    private DatePicker mulaiDatePicker;

    @FXML
    private DatePicker selesaiDatePicker;

    @FXML
    private TextField totalKunjunganField;

    @FXML
    private TextField totalPasienBaruField;

    @FXML
    private TextArea keteranganArea;

    private final LaporanKunjunganDAO laporanDAO = new LaporanKunjunganDAO();
    private final DokterDAO dokterDAO = new DokterDAO();
    private LaporanKunjungan selectedLaporan = null;

    @FXML
    public void initialize() {
        // 1. Konfigurasi kolom tabel
        colDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        colKunjungan.setCellValueFactory(new PropertyValueFactory<>("totalKunjungan"));
        colPasienBaru.setCellValueFactory(new PropertyValueFactory<>("totalPasienBaru"));
        
        // Custom rendering untuk kolom Periode
        colPeriode.setCellValueFactory(cellData -> {
            LaporanKunjungan l = cellData.getValue();
            if (l.getPeriodeMulai() != null && l.getPeriodeSelesai() != null) {
                return new SimpleStringProperty(l.getPeriodeMulai().toString() + " s/d " + l.getPeriodeSelesai().toString());
            }
            return new SimpleStringProperty("-");
        });

        // 2. Setup dropdown dokter
        setupDokterComboBox();

        // 3. Selection listener pada tabel
        laporanTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLaporan = newVal;
                showLaporanDetails(newVal);
            }
        });

        // 4. Load data awal
        loadLaporanData();
        clearForm();
    }

    private void setupDokterComboBox() {
        List<Dokter> list = dokterDAO.findAll();
        dokterComboBox.setItems(FXCollections.observableArrayList(list));
        dokterComboBox.setConverter(new StringConverter<Dokter>() {
            @Override public String toString(Dokter d) {
                return d != null ? d.getNamaLengkap() + " (" + d.getNamaSpesialisasi() + ")" : "";
            }
            @Override public Dokter fromString(String s) { return null; }
        });
    }

    private void loadLaporanData() {
        List<LaporanKunjungan> list = laporanDAO.findAll();
        laporanTableView.setItems(FXCollections.observableArrayList(list));
    }

    private void showLaporanDetails(LaporanKunjungan l) {
        for (Dokter d : dokterComboBox.getItems()) {
            if (d.getIdDokter() == l.getIdDokter()) {
                dokterComboBox.setValue(d);
                break;
            }
        }
        mulaiDatePicker.setValue(l.getPeriodeMulai());
        selesaiDatePicker.setValue(l.getPeriodeSelesai());
        totalKunjunganField.setText(String.valueOf(l.getTotalKunjungan()));
        totalPasienBaruField.setText(String.valueOf(l.getTotalPasienBaru()));
        keteranganArea.setText(l.getKeterangan());
    }

    private void clearForm() {
        selectedLaporan = null;
        dokterComboBox.setValue(null);
        mulaiDatePicker.setValue(null);
        selesaiDatePicker.setValue(null);
        totalKunjunganField.setText("");
        totalPasienBaruField.setText("");
        keteranganArea.setText("");
        laporanTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleHitungStatistik(ActionEvent event) {
        Dokter d = dokterComboBox.getValue();
        LocalDate mulai = mulaiDatePicker.getValue();
        LocalDate selesai = selesaiDatePicker.getValue();

        if (d == null || mulai == null || selesai == null) {
            AlertHelper.showWarning("Validasi Gagal", "Input Kurang", "Pilih Dokter, Tanggal Mulai, dan Tanggal Selesai terlebih dahulu!");
            return;
        }

        if (selesai.isBefore(mulai)) {
            AlertHelper.showWarning("Validasi Gagal", "Format Tanggal", "Tanggal Selesai tidak boleh mendahului Tanggal Mulai!");
            return;
        }

        // Lakukan agregasi perhitungan realtime dari database
        LaporanKunjungan stats = laporanDAO.hitungStatistik(d.getIdDokter(), mulai, selesai);
        totalKunjunganField.setText(String.valueOf(stats.getTotalKunjungan()));
        totalPasienBaruField.setText(String.valueOf(stats.getTotalPasienBaru()));
    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selectedLaporan == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Laporan", "Silakan pilih baris laporan di tabel yang ingin dihapus terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Hapus Laporan", "Konfirmasi Hapus", 
                "Apakah Anda yakin ingin menghapus data riwayat laporan ini dari database?");
        if (confirm) {
            boolean success = laporanDAO.delete(selectedLaporan.getIdLaporan());
            if (success) {
                AlertHelper.showInfo("Sukses", "Laporan Dihapus", "Riwayat laporan berhasil dihapus.");
                loadLaporanData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Hapus", "Terjadi kesalahan saat menghapus laporan.");
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        Dokter d = dokterComboBox.getValue();
        LocalDate mulai = mulaiDatePicker.getValue();
        LocalDate selesai = selesaiDatePicker.getValue();
        String totalKunjStr = totalKunjunganField.getText();
        String totalPasStr = totalPasienBaruField.getText();

        if (d == null || mulai == null || selesai == null || totalKunjStr.isEmpty() || totalPasStr.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Laporan Belum Dihitung", "Silakan klik tombol 'Hitung Realtime' terlebih dahulu sebelum menyimpan!");
            return;
        }

        if (selectedLaporan != null) {
            AlertHelper.showWarning("Peringatan", "Laporan Terkunci", "Riwayat laporan yang telah disimpan tidak dapat diedit ulang. Buatlah laporan baru.");
            return;
        }

        // Simpan
        LaporanKunjungan l = new LaporanKunjungan();
        l.setIdDokter(d.getIdDokter());
        l.setPeriodeMulai(mulai);
        l.setPeriodeSelesai(selesai);
        l.setTotalKunjungan(Integer.parseInt(totalKunjStr));
        l.setTotalPasienBaru(Integer.parseInt(totalPasStr));
        l.setKeterangan(keteranganArea.getText().trim());
        
        if (SessionManager.isLoggedIn()) {
            l.setDibuatOleh(SessionManager.getCurrentUser().getIdPengguna());
        }

        boolean success = laporanDAO.insert(l);
        if (success) {
            AlertHelper.showInfo("Sukses", "Laporan Disimpan", "Laporan periode " + mulai + " s/d " + selesai + " berhasil disimpan.");
            loadLaporanData();
            clearForm();
        } else {
            AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal menyimpan laporan ke database.");
        }
    }
}
