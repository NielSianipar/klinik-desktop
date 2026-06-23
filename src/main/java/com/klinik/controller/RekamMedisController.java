package com.klinik.controller;

import com.klinik.dao.*;
import com.klinik.model.*;
import com.klinik.util.AlertHelper;
import com.klinik.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller untuk menangani rekam medis pemeriksaan pasien oleh Dokter.
 * 
 * @author Daniel sianipar
 */
public class RekamMedisController {

    // PANEL KIRI: Antrian Pasien
    @FXML
    private TableView<Pendaftaran> antrianTableView;

    @FXML
    private TableColumn<Pendaftaran, String> colNoAntrian;

    @FXML
    private TableColumn<Pendaftaran, String> colNamaPasien;

    // PANEL KANAN: Label Identitas Pasien
    @FXML
    private Label lblNamaPasien;

    @FXML
    private Label lblRmPasien;

    @FXML
    private Label lblAlergiPasien;

    @FXML
    private Label lblUsiaGolDarah;

    @FXML
    private TabPane rekamMedisTabPane;

    // TAB 1: Pemeriksaan Baru (Vitals)
    @FXML
    private TextField tdField;

    @FXML
    private TextField suhuField;

    @FXML
    private TextField bbField;

    @FXML
    private TextField tbField;

    // Diagnosa & Catatan
    @FXML
    private TextArea anamnesisArea;

    @FXML
    private TextArea fisikArea;

    @FXML
    private TextField diagnosisField;

    @FXML
    private TextField icdField;

    @FXML
    private TextField tindakanField;

    @FXML
    private TextField catatanField;

    // Resep Obat
    @FXML
    private ComboBox<Obat> obatComboBox;

    @FXML
    private TextField obatJumlahField;

    @FXML
    private TextField obatAturanField;

    @FXML
    private TableView<RiwayatPengobatan> resepTableView;

    @FXML
    private TableColumn<RiwayatPengobatan, String> colResepObat;

    @FXML
    private TableColumn<RiwayatPengobatan, Integer> colResepJumlah;

    @FXML
    private TableColumn<RiwayatPengobatan, String> colResepAturan;

    // TAB 2: Riwayat Pemeriksaan
    @FXML
    private TableView<RekamMedis> riwayatTableView;

    @FXML
    private TableColumn<RekamMedis, String> colRiwayatTanggal;

    @FXML
    private TableColumn<RekamMedis, String> colRiwayatDokter;

    @FXML
    private TableColumn<RekamMedis, String> colRiwayatDiagnosis;

    @FXML
    private TableColumn<RekamMedis, String> colRiwayatCatatan;

    // DAOs
    private final PendaftaranDAO pendaftaranDAO = new PendaftaranDAO();
    private final PasienDAO pasienDAO = new PasienDAO();
    private final DokterDAO dokterDAO = new DokterDAO();
    private final ObatDAO obatDAO = new ObatDAO();
    private final RekamMedisDAO rekamMedisDAO = new RekamMedisDAO();
    private final RiwayatPengobatanDAO riwayatPengobatanDAO = new RiwayatPengobatanDAO();

    // States
    private Dokter currentDokter = null;
    private Pendaftaran selectedPendaftaran = null;
    private Pasien selectedPasien = null;
    private final ObservableList<RiwayatPengobatan> resepList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Dapatkan profil dokter aktif yang login
        if (SessionManager.isLoggedIn()) {
            Pengguna user = SessionManager.getCurrentUser();
            currentDokter = dokterDAO.findByPenggunaId(user.getIdPengguna());
        }

        // 2. Setup tabel antrian
        colNoAntrian.setCellValueFactory(new PropertyValueFactory<>("nomorAntrian"));
        colNamaPasien.setCellValueFactory(new PropertyValueFactory<>("namaPasien"));

        // Listener klik antrian
        antrianTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedPendaftaran = newVal;
                loadPatientInfo(newVal.getIdPasien());
            }
        });

        // 3. Setup tabel resep obat
        colResepObat.setCellValueFactory(new PropertyValueFactory<>("namaObat"));
        colResepJumlah.setCellValueFactory(new PropertyValueFactory<>("jumlah"));
        colResepAturan.setCellValueFactory(new PropertyValueFactory<>("aturanPakai"));
        resepTableView.setItems(resepList);

        // Setup combobox obat apotek
        setupObatComboBox();

        // 4. Setup tabel riwayat
        colRiwayatTanggal.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTanggalPeriksa().toString()));
        colRiwayatDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        colRiwayatDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colRiwayatCatatan.setCellValueFactory(new PropertyValueFactory<>("catatan"));

        // 5. Load antrian dokter hari ini
        loadDoctorQueue();
    }

    private void setupObatComboBox() {
        List<Obat> list = obatDAO.findAll().stream().filter(Obat::isAktif).collect(Collectors.toList());
        obatComboBox.setItems(FXCollections.observableArrayList(list));
        obatComboBox.setConverter(new StringConverter<Obat>() {
            @Override public String toString(Obat o) {
                return o != null ? o.getNamaObat() + " (" + o.getSatuan() + ") - Stok: " + o.getStok() : "";
            }
            @Override public Obat fromString(String s) { return null; }
        });
    }

    private void loadDoctorQueue() {
        if (currentDokter == null) {
            antrianTableView.getItems().clear();
            return;
        }

        // Load antrian hari ini untuk dokter aktif yang statusnya 'menunggu' atau 'dipanggil'
        List<Pendaftaran> list = pendaftaranDAO.findByDokterAndDate(currentDokter.getIdDokter(), LocalDate.now())
                .stream()
                .filter(p -> "menunggu".equalsIgnoreCase(p.getStatus()) || "dipanggil".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());
        
        antrianTableView.setItems(FXCollections.observableArrayList(list));
    }

    private void loadPatientInfo(int idPasien) {
        selectedPasien = pasienDAO.findById(idPasien);
        if (selectedPasien != null) {
            lblNamaPasien.setText("Nama Pasien: " + selectedPasien.getNamaLengkap());
            lblRmPasien.setText("No. RM: " + selectedPasien.getNomorRm());
            
            String alergi = selectedPasien.getRiwayatAlergi();
            lblAlergiPasien.setText("Alergi: " + (alergi == null || alergi.isEmpty() ? "Tidak Ada" : alergi));
            
            lblUsiaGolDarah.setText("Usia: " + selectedPasien.getUsia() + " Th  |  Gol Darah: " + selectedPasien.getGolonganDarah());

            // Load riwayat rekam medis pasien di Tab 2
            loadPatientHistory(idPasien);
        }
        
        // Reset form input rekam medis baru
        handleClearForm(null);
    }

    private void loadPatientHistory(int idPasien) {
        List<RekamMedis> riwayat = rekamMedisDAO.findByPasienId(idPasien);
        riwayatTableView.setItems(FXCollections.observableArrayList(riwayat));
    }

    @FXML
    void handleRefreshAntrian(ActionEvent event) {
        loadDoctorQueue();
    }

    @FXML
    void handleAddObat(ActionEvent event) {
        Obat o = obatComboBox.getValue();
        String qtyStr = obatJumlahField.getText().trim();
        String aturan = obatAturanField.getText().trim();

        if (o == null || qtyStr.isEmpty() || aturan.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Input Resep", "Obat, Jumlah, dan Aturan Pakai resep harus diisi!");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validasi Gagal", "Format Jumlah", "Jumlah obat resep harus berupa angka bulat positif!");
            return;
        }

        // Cek stok obat mencukupi
        if (o.getStok() < qty) {
            AlertHelper.showWarning("Stok Tidak Cukup", "Stok Kurang", "Stok obat " + o.getNamaObat() + " di apotek tidak mencukupi (Tersedia: " + o.getStok() + ")!");
            return;
        }

        // Buat objek RiwayatPengobatan resep sementara
        RiwayatPengobatan rp = new RiwayatPengobatan();
        rp.setIdObat(o.getIdObat());
        rp.setJumlah(qty);
        rp.setAturanPakai(aturan);
        
        // Simpan join fields untuk tampilan di TableView
        rp.setNamaObat(o.getNamaObat());
        rp.setSatuan(o.getSatuan());
        rp.setKodeObat(o.getKodeObat());

        resepList.add(rp);

        // Reset input resep
        obatComboBox.setValue(null);
        obatJumlahField.setText("");
        obatAturanField.setText("");
    }

    @FXML
    void handleRemoveObat(ActionEvent event) {
        RiwayatPengobatan selectedRp = resepTableView.getSelectionModel().getSelectedItem();
        if (selectedRp != null) {
            resepList.remove(selectedRp);
        } else {
            AlertHelper.showWarning("Peringatan", "Pilih Obat", "Silakan pilih baris resep obat yang ingin dihapus dari daftar.");
        }
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        tdField.setText("");
        suhuField.setText("");
        bbField.setText("");
        tbField.setText("");
        anamnesisArea.setText("");
        fisikArea.setText("");
        diagnosisField.setText("");
        icdField.setText("");
        tindakanField.setText("");
        catatanField.setText("");
        resepList.clear();
        obatComboBox.setValue(null);
        obatJumlahField.setText("");
        obatAturanField.setText("");
    }

    @FXML
    void handleSavePemeriksaan(ActionEvent event) {
        if (selectedPendaftaran == null || selectedPasien == null || currentDokter == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Antrian", "Silakan pilih pasien dari tabel antrian terlebih dahulu!");
            return;
        }

        String anamnesis = anamnesisArea.getText().trim();
        String diagnosis = diagnosisField.getText().trim();

        if (anamnesis.isEmpty() || diagnosis.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Pemeriksaan Belum Lengkap", "Anamnesis dan Diagnosis Utama wajib diisi!");
            return;
        }

        // Konversi vitals angka desimal (opsional)
        BigDecimal suhu = null;
        BigDecimal berat = null;
        BigDecimal tinggi = null;

        try {
            if (!suhuField.getText().trim().isEmpty()) suhu = new BigDecimal(suhuField.getText().trim());
            if (!bbField.getText().trim().isEmpty()) berat = new BigDecimal(bbField.getText().trim());
            if (!tbField.getText().trim().isEmpty()) tinggi = new BigDecimal(tbField.getText().trim());
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Format Angka Salah", "Vital Sign Error", "Suhu, Berat Badan, dan Tinggi Badan harus berupa angka desimal!");
            return;
        }

        // Buat objek Rekam Medis
        RekamMedis rm = new RekamMedis();
        rm.setIdPendaftaran(selectedPendaftaran.getIdPendaftaran());
        rm.setIdDokter(currentDokter.getIdDokter());
        rm.setIdPasien(selectedPasien.getIdPasien());
        rm.setTanggalPeriksa(LocalDate.now());
        rm.setTekananDarah(tdField.getText().trim());
        rm.setSuhuTubuh(suhu);
        rm.setBeratBadan(berat);
        rm.setTinggiBadan(tinggi);
        rm.setAnamnesis(anamnesis);
        rm.setPemeriksaanFisik(fisikArea.getText().trim());
        rm.setDiagnosis(diagnosis);
        rm.setKodeIcd(icdField.getText().trim());
        rm.setTindakan(tindakanField.getText().trim());
        rm.setCatatan(catatanField.getText().trim());

        // Jalankan penyimpanan rekam medis + resep + potong stok + update antrian status
        boolean success = rekamMedisDAO.insert(rm);
        
        if (success) {
            // Simpan detail resep ke database jika ada
            for (RiwayatPengobatan rp : resepList) {
                rp.setIdRekamMedis(rm.getIdRekamMedis());
                riwayatPengobatanDAO.insert(rp);
                
                // Potong stok obat di apotek secara otomatis
                obatDAO.updateStok(rp.getIdObat(), -rp.getJumlah());
            }

            // Update status antrian kunjungan menjadi 'selesai'
            pendaftaranDAO.updateStatus(selectedPendaftaran.getIdPendaftaran(), "selesai");

            AlertHelper.showInfo("Pemeriksaan Selesai", "Rekam Medis Disimpan", "Pemeriksaan pasien berhasil disimpan dan antrian telah diselesaikan.");
            
            // Refresh screen
            loadDoctorQueue();
            setupObatComboBox(); // segarkan list obat agar stok terupdate
            
            // Reset state
            selectedPendaftaran = null;
            selectedPasien = null;
            lblNamaPasien.setText("Nama Pasien: -");
            lblRmPasien.setText("No. RM: -");
            lblAlergiPasien.setText("Alergi: -");
            lblUsiaGolDarah.setText("Usia: -  |  Gol Darah: -");
            handleClearForm(null);
            riwayatTableView.getItems().clear();
        } else {
            AlertHelper.showError("Error", "Gagal Menyimpan", "Terjadi kesalahan saat menyimpan rekam medis pemeriksaan.");
        }
    }
}
