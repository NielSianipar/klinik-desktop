package com.klinik.controller;

import com.klinik.dao.DokterDAO;
import com.klinik.dao.JadwalDokterDAO;
import com.klinik.dao.PasienDAO;
import com.klinik.dao.PendaftaranDAO;
import com.klinik.model.Dokter;
import com.klinik.model.JadwalDokter;
import com.klinik.model.Pasien;
import com.klinik.model.Pendaftaran;
import com.klinik.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller untuk mengelola pendaftaran antrian kunjungan pasien.
 * 
 * @author Daniel sianipar
 */
public class PendaftaranController {

    @FXML
    private DatePicker filterDatePicker;

    @FXML
    private TableView<Pendaftaran> pendaftaranTableView;

    @FXML
    private TableColumn<Pendaftaran, String> colAntrian;

    @FXML
    private TableColumn<Pendaftaran, String> colPasien;

    @FXML
    private TableColumn<Pendaftaran, String> colDokter;

    @FXML
    private TableColumn<Pendaftaran, String> colStatus;

    // Form fields
    @FXML
    private DatePicker kunjunganDatePicker;

    @FXML
    private ComboBox<Pasien> pasienComboBox;

    @FXML
    private ComboBox<Dokter> dokterComboBox;

    @FXML
    private ComboBox<JadwalDokter> jadwalComboBox;

    @FXML
    private TextField noAntrianField;

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private TextArea keluhanArea;

    @FXML
    private TextArea catatanArea;

    // DAOs
    private final PendaftaranDAO pendaftaranDAO = new PendaftaranDAO();
    private final PasienDAO pasienDAO = new PasienDAO();
    private final DokterDAO dokterDAO = new DokterDAO();
    private final JadwalDokterDAO jadwalDAO = new JadwalDokterDAO();

    private Pendaftaran selectedPendaftaran = null;

    @FXML
    public void initialize() {
        // 1. Konfigurasi kolom tabel
        colAntrian.setCellValueFactory(new PropertyValueFactory<>("nomorAntrian"));
        colPasien.setCellValueFactory(new PropertyValueFactory<>("namaPasien"));
        colDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Setup items dropdown
        setupFormDropdowns();

        // 3. Set filter & tanggal default hari ini
        filterDatePicker.setValue(LocalDate.now());
        kunjunganDatePicker.setValue(LocalDate.now());

        // 4. Listeners untuk update form
        setupFormListeners();

        // 5. Load data antrian pertama kali
        loadQueueData();
        clearForm();
    }

    private void setupFormDropdowns() {
        // Pasien Dropdown
        List<Pasien> pasiens = pasienDAO.findAll();
        pasienComboBox.setItems(FXCollections.observableArrayList(pasiens));
        pasienComboBox.setConverter(new StringConverter<Pasien>() {
            @Override public String toString(Pasien p) {
                return p != null ? p.getNamaLengkap() + " (" + p.getNomorRm() + ")" : "";
            }
            @Override public Pasien fromString(String s) { return null; }
        });

        // Dokter Dropdown
        List<Dokter> dokters = dokterDAO.findAll();
        dokterComboBox.setItems(FXCollections.observableArrayList(dokters));
        dokterComboBox.setConverter(new StringConverter<Dokter>() {
            @Override public String toString(Dokter d) {
                return d != null ? d.getNamaLengkap() + " (" + d.getNamaSpesialisasi() + ")" : "";
            }
            @Override public Dokter fromString(String s) { return null; }
        });

        // Jadwal Dropdown converter
        jadwalComboBox.setConverter(new StringConverter<JadwalDokter>() {
            @Override public String toString(JadwalDokter j) {
                if (j == null) return "";
                DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
                return j.getHari().toUpperCase() + ": " + j.getJamMulai().format(tf) + " - " + j.getJamSelesai().format(tf);
            }
            @Override public JadwalDokter fromString(String s) { return null; }
        });

        // Status Dropdown
        statusComboBox.setItems(FXCollections.observableArrayList("menunggu", "dipanggil", "selesai", "batal"));
    }

    private void setupFormListeners() {
        // Listener tabel selection
        pendaftaranTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPendaftaran = newVal;
                showPendaftaranDetails(newVal);
            }
        });

        // Filter tanggal untuk membatasi antrian di tabel
        filterDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) loadQueueData();
        });

        // Jika user memilih dokter, load jadwal milik dokter tersebut
        dokterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadJadwalForDokter(newVal.getIdDokter());
                generateQueueNumber();
            } else {
                jadwalComboBox.getItems().clear();
            }
        });

        // Jika user mengubah tanggal pendaftaran, perbarui no antrian
        kunjunganDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            generateQueueNumber();
        });
    }

    private void loadJadwalForDokter(int idDokter) {
        List<JadwalDokter> jadwals = jadwalDAO.findByDokterId(idDokter);
        jadwalComboBox.setItems(FXCollections.observableArrayList(jadwals));
    }

    private void generateQueueNumber() {
        Dokter d = dokterComboBox.getValue();
        LocalDate tanggal = kunjunganDatePicker.getValue();
        if (d != null && tanggal != null && selectedPendaftaran == null) {
            String nextNo = pendaftaranDAO.generateNomorAntrian(d.getIdDokter(), tanggal);
            noAntrianField.setText(nextNo);
        }
    }

    private void loadQueueData() {
        LocalDate filterDate = filterDatePicker.getValue();
        if (filterDate != null) {
            List<Pendaftaran> queue = pendaftaranDAO.findByDate(filterDate);
            pendaftaranTableView.setItems(FXCollections.observableArrayList(queue));
        }
    }

    private void showPendaftaranDetails(Pendaftaran p) {
        kunjunganDatePicker.setValue(p.getTanggalKunjungan());
        
        // Cari Pasien
        for (Pasien pas : pasienComboBox.getItems()) {
            if (pas.getIdPasien() == p.getIdPasien()) {
                pasienComboBox.setValue(pas);
                break;
            }
        }
        
        // Cari Dokter
        for (Dokter d : dokterComboBox.getItems()) {
            if (d.getIdDokter() == p.getIdDokter()) {
                dokterComboBox.setValue(d);
                break;
            }
        }

        // Cari Jadwal
        for (JadwalDokter j : jadwalComboBox.getItems()) {
            if (j.getIdJadwal() == p.getIdJadwal()) {
                jadwalComboBox.setValue(j);
                break;
            }
        }

        noAntrianField.setText(p.getNomorAntrian());
        statusComboBox.setValue(p.getStatus());
        keluhanArea.setText(p.getKeluhan());
        catatanArea.setText(p.getCatatanAdmin());
    }

    private void clearForm() {
        selectedPendaftaran = null;
        kunjunganDatePicker.setValue(LocalDate.now());
        pasienComboBox.setValue(null);
        dokterComboBox.setValue(null);
        jadwalComboBox.setValue(null);
        noAntrianField.setText("");
        statusComboBox.setValue("menunggu");
        keluhanArea.setText("");
        catatanArea.setText("");
        pendaftaranTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleRefreshQueue(ActionEvent event) {
        loadQueueData();
    }

    @FXML
    void handleNew(ActionEvent event) {
        clearForm();
        generateQueueNumber();
    }

    @FXML
    void handleCallPatient(ActionEvent event) {
        if (selectedPendaftaran == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Antrian", "Silakan pilih pasien di tabel antrian terlebih dahulu.");
            return;
        }

        boolean success = pendaftaranDAO.updateStatus(selectedPendaftaran.getIdPendaftaran(), "dipanggil");
        if (success) {
            AlertHelper.showInfo("Sukses", "Panggil Pasien", "Pasien " + selectedPendaftaran.getNamaPasien() + " dipanggil ke ruang periksa.");
            loadQueueData();
            clearForm();
        } else {
            AlertHelper.showError("Error", "Gagal Panggil", "Gagal memperbarui status antrian.");
        }
    }

    @FXML
    void handleCancelPatient(ActionEvent event) {
        if (selectedPendaftaran == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Antrian", "Silakan pilih pasien di tabel antrian terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Batal Kunjungan", "Konfirmasi Pembatalan", 
                "Apakah Anda yakin ingin membatalkan pendaftaran untuk " + selectedPendaftaran.getNamaPasien() + "?");
        if (confirm) {
            boolean success = pendaftaranDAO.updateStatus(selectedPendaftaran.getIdPendaftaran(), "batal");
            if (success) {
                AlertHelper.showInfo("Sukses", "Batal Sukses", "Pendaftaran telah dibatalkan.");
                loadQueueData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Batal", "Gagal membatalkan pendaftaran.");
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        Pasien p = pasienComboBox.getValue();
        Dokter d = dokterComboBox.getValue();
        JadwalDokter j = jadwalComboBox.getValue();
        LocalDate tgl = kunjunganDatePicker.getValue();
        String status = statusComboBox.getValue();
        String keluhan = keluhanArea.getText().trim();
        String catatan = catatanArea.getText().trim();

        if (p == null || d == null || j == null || tgl == null || status == null) {
            AlertHelper.showWarning("Validasi Gagal", "Input Tidak Lengkap", "Pasien, Dokter, Jadwal Praktik, Tanggal, dan Status harus diisi!");
            return;
        }

        if (selectedPendaftaran == null) {
            // INSERT
            Pendaftaran pen = new Pendaftaran();
            pen.setIdPasien(p.getIdPasien());
            pen.setIdDokter(d.getIdDokter());
            pen.setIdJadwal(j.getIdJadwal());
            pen.setTanggalKunjungan(tgl);
            pen.setNomorAntrian(noAntrianField.getText());
            pen.setStatus(status);
            pen.setKeluhan(keluhan);
            pen.setCatatanAdmin(catatan);

            boolean success = pendaftaranDAO.insert(pen);
            if (success) {
                AlertHelper.showInfo("Sukses", "Pendaftaran Berhasil", "Pasien berhasil didaftarkan ke antrian.");
                filterDatePicker.setValue(tgl); // Otomatis segarkan filter ke tanggal pendaftaran
                loadQueueData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Daftar", "Gagal menyimpan pendaftaran baru.");
            }
        } else {
            // UPDATE
            selectedPendaftaran.setIdPasien(p.getIdPasien());
            selectedPendaftaran.setIdDokter(d.getIdDokter());
            selectedPendaftaran.setIdJadwal(j.getIdJadwal());
            selectedPendaftaran.setTanggalKunjungan(tgl);
            selectedPendaftaran.setNomorAntrian(noAntrianField.getText());
            selectedPendaftaran.setStatus(status);
            selectedPendaftaran.setKeluhan(keluhan);
            selectedPendaftaran.setCatatanAdmin(catatan);

            boolean success = pendaftaranDAO.update(selectedPendaftaran);
            if (success) {
                AlertHelper.showInfo("Sukses", "Pendaftaran Diupdate", "Data pendaftaran berhasil diperbarui.");
                filterDatePicker.setValue(tgl); // Otomatis segarkan filter ke tanggal pendaftaran
                loadQueueData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Update", "Terjadi kesalahan saat memperbarui pendaftaran.");
            }
        }
    }
}
