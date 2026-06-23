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
import javafx.scene.layout.VBox;

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
    private VBox leftQueuePanel;

    @FXML
    private Tab tabPemeriksaan;

    @FXML
    private Tab tabRiwayat;

    @FXML
    private Label lblAntrianTitle;

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

    @FXML
    private Button btnTambahObat;

    @FXML
    private Button btnHapusObat;

    @FXML
    private Button btnResetForm;

    @FXML
    private Button btnSimpanPemeriksaan;

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
        // 1. Dapatkan profil user login
        boolean isPerawat = false;
        boolean isPasien = false;
        Pasien patientUser = null;
        if (SessionManager.isLoggedIn()) {
            Pengguna user = SessionManager.getCurrentUser();
            isPerawat = "perawat".equalsIgnoreCase(user.getPeran());
            isPasien = "pasien".equalsIgnoreCase(user.getPeran());
            if (isPasien) {
                patientUser = pasienDAO.findByPenggunaId(user.getIdPengguna());
                if (patientUser == null && user.getEmail() != null) {
                    patientUser = pasienDAO.findByEmail(user.getEmail());
                    if (patientUser != null) {
                        patientUser.setIdPengguna(user.getIdPengguna());
                        pasienDAO.update(patientUser);
                    }
                }
            } else {
                currentDokter = dokterDAO.findByPenggunaId(user.getIdPengguna());
            }
        }

        if (isPerawat) {
            lblAntrianTitle.setText("Semua Antrian Hari Ini");
            btnTambahObat.setVisible(false);
            btnTambahObat.setManaged(false);
            btnHapusObat.setVisible(false);
            btnHapusObat.setManaged(false);
            btnResetForm.setVisible(false);
            btnResetForm.setManaged(false);
            btnSimpanPemeriksaan.setVisible(false);
            btnSimpanPemeriksaan.setManaged(false);
        } else if (isPasien) {
            // Sembunyikan panel antrian kiri
            leftQueuePanel.setVisible(false);
            leftQueuePanel.setManaged(false);
            
            // Sembunyikan tombol manipulasi
            btnTambahObat.setVisible(false);
            btnTambahObat.setManaged(false);
            btnHapusObat.setVisible(false);
            btnHapusObat.setManaged(false);
            btnResetForm.setVisible(false);
            btnResetForm.setManaged(false);
            btnSimpanPemeriksaan.setVisible(false);
            btnSimpanPemeriksaan.setManaged(false);
            
            // Ubah nama tab
            tabPemeriksaan.setText("📋 Detail Rincian Berobat");
            
            // Set form read-only
            setFormEditable(false);
            
            // Load detail pasien terpilih & riwayatnya
            if (patientUser != null) {
                selectedPasien = patientUser;
                lblNamaPasien.setText("Nama Pasien: " + selectedPasien.getNamaLengkap());
                lblRmPasien.setText("No. RM: " + selectedPasien.getNomorRm());
                String alergi = selectedPasien.getRiwayatAlergi();
                lblAlergiPasien.setText("Alergi: " + (alergi == null || alergi.isEmpty() ? "Tidak Ada" : alergi));
                lblUsiaGolDarah.setText("Usia: " + selectedPasien.getUsia() + " Th  |  Gol Darah: " + selectedPasien.getGolonganDarah());
                
                loadPatientHistory(selectedPasien.getIdPasien());
            }
        }

        // 2. Setup tabel antrian
        colNoAntrian.setCellValueFactory(new PropertyValueFactory<>("nomorAntrian"));
        colNamaPasien.setCellValueFactory(cellData -> {
            Pendaftaran p = cellData.getValue();
            if (p != null) {
                String name = p.getNamaPasien();
                if ("selesai".equalsIgnoreCase(p.getStatus())) {
                    name += " (Selesai)";
                } else if ("dipanggil".equalsIgnoreCase(p.getStatus())) {
                    name += " (Dipanggil)";
                }
                return new SimpleStringProperty(name);
            }
            return new SimpleStringProperty("");
        });

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
        colRiwayatTanggal.setCellValueFactory(cellData -> {
            if (cellData.getValue() != null && cellData.getValue().getTanggalPeriksa() != null) {
                return new SimpleStringProperty(cellData.getValue().getTanggalPeriksa().toString());
            }
            return new SimpleStringProperty("-");
        });
        colRiwayatDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        colRiwayatDiagnosis.setCellValueFactory(new PropertyValueFactory<>("diagnosis"));
        colRiwayatCatatan.setCellValueFactory(new PropertyValueFactory<>("catatan"));

        // Listener klik riwayat untuk memuat detail rincian ke Tab 1 secara otomatis
        riwayatTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                displayRekamMedisDetails(newVal);
            }
        });

        // 5. Load antrian dokter hari ini
        if (!isPasien) {
            loadDoctorQueue();
        } else {
            // Pindahkan tab aktif secara default ke Tab Riwayat Rekam Medis
            rekamMedisTabPane.getSelectionModel().select(tabRiwayat);
        }
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
        boolean isPerawat = SessionManager.isLoggedIn() && "perawat".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran());
        
        if (currentDokter == null && !isPerawat) {
            antrianTableView.getItems().clear();
            return;
        }

        List<Pendaftaran> list;
        if (isPerawat) {
            // Perawat dapat melihat semua antrian hari ini dari semua dokter
            list = pendaftaranDAO.findByDate(LocalDate.now())
                    .stream()
                    .filter(p -> "menunggu".equalsIgnoreCase(p.getStatus()) || "dipanggil".equalsIgnoreCase(p.getStatus()) || "selesai".equalsIgnoreCase(p.getStatus()))
                    .collect(Collectors.toList());
        } else {
            // Dokter hanya melihat antrian miliknya sendiri
            list = pendaftaranDAO.findByDokterAndDate(currentDokter.getIdDokter(), LocalDate.now())
                    .stream()
                    .filter(p -> "menunggu".equalsIgnoreCase(p.getStatus()) || "dipanggil".equalsIgnoreCase(p.getStatus()) || "selesai".equalsIgnoreCase(p.getStatus()))
                    .collect(Collectors.toList());
        }
        
        antrianTableView.setItems(FXCollections.observableArrayList(list));
    }

    private void loadPatientInfo(int idPasien) {
        boolean isPerawat = SessionManager.isLoggedIn() && "perawat".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran());
        selectedPasien = pasienDAO.findById(idPasien);
        if (selectedPasien != null) {
            lblNamaPasien.setText("Nama Pasien: " + selectedPasien.getNamaLengkap());
            lblRmPasien.setText("No. RM: " + selectedPasien.getNomorRm());
            
            String alergi = selectedPasien.getRiwayatAlergi();
            lblAlergiPasien.setText("Alergi: " + (alergi == null || alergi.isEmpty() ? "Tidak Ada" : alergi));
            
            lblUsiaGolDarah.setText("Usia: " + selectedPasien.getUsia() + " Th  |  Gol Darah: " + selectedPasien.getGolonganDarah());

            // Load riwayat rekam medis pasien di Tab 2
            loadPatientHistory(idPasien);

            if (selectedPendaftaran != null && "selesai".equalsIgnoreCase(selectedPendaftaran.getStatus())) {
                loadSavedPemeriksaan(selectedPendaftaran.getIdPendaftaran());
            } else {
                handleClearForm(null);
                setFormEditable(!isPerawat);
            }
        } else {
            handleClearForm(null);
            setFormEditable(!isPerawat);
        }
    }

    private void loadPatientHistory(int idPasien) {
        List<RekamMedis> riwayat = rekamMedisDAO.findByPasienId(idPasien);
        riwayatTableView.setItems(FXCollections.observableArrayList(riwayat));
    }

    private void loadSavedPemeriksaan(int idPendaftaran) {
        RekamMedis rm = rekamMedisDAO.findByPendaftaranId(idPendaftaran);
        if (rm != null) {
            displayRekamMedisDetails(rm);
        } else {
            handleClearForm(null);
            setFormEditable(true);
        }
    }

    private void displayRekamMedisDetails(RekamMedis rm) {
        if (rm != null) {
            tdField.setText(rm.getTekananDarah() != null ? rm.getTekananDarah() : "");
            suhuField.setText(rm.getSuhuTubuh() != null ? rm.getSuhuTubuh().toString() : "");
            bbField.setText(rm.getBeratBadan() != null ? rm.getBeratBadan().toString() : "");
            tbField.setText(rm.getTinggiBadan() != null ? rm.getTinggiBadan().toString() : "");
            anamnesisArea.setText(rm.getAnamnesis() != null ? rm.getAnamnesis() : "");
            fisikArea.setText(rm.getPemeriksaanFisik() != null ? rm.getPemeriksaanFisik() : "");
            diagnosisField.setText(rm.getDiagnosis() != null ? rm.getDiagnosis() : "");
            icdField.setText(rm.getKodeIcd() != null ? rm.getKodeIcd() : "");
            tindakanField.setText(rm.getTindakan() != null ? rm.getTindakan() : "");
            catatanField.setText(rm.getCatatan() != null ? rm.getCatatan() : "");
            
            // Load prescribed drugs for this rekam medis
            resepList.clear();
            List<RiwayatPengobatan> reseps = riwayatPengobatanDAO.findByRekamMedisId(rm.getIdRekamMedis());
            resepList.addAll(reseps);
            
            // Set form to read-only
            setFormEditable(false);
            
            // Auto switch tab to the Pemeriksaan/Rincian tab so the user sees the details immediately
            rekamMedisTabPane.getSelectionModel().select(tabPemeriksaan);
        }
    }

    private void setFormEditable(boolean editable) {
        tdField.setEditable(editable);
        suhuField.setEditable(editable);
        bbField.setEditable(editable);
        tbField.setEditable(editable);
        anamnesisArea.setEditable(editable);
        fisikArea.setEditable(editable);
        diagnosisField.setEditable(editable);
        icdField.setEditable(editable);
        tindakanField.setEditable(editable);
        catatanField.setEditable(editable);
        obatComboBox.setDisable(!editable);
        obatJumlahField.setEditable(editable);
        obatAturanField.setEditable(editable);
    }

    @FXML
    void handleRefreshAntrian(ActionEvent event) {
        loadDoctorQueue();
    }

    @FXML
    void handleAddObat(ActionEvent event) {
        boolean isRestricted = SessionManager.isLoggedIn() && 
            ("perawat".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran()) || 
             "pasien".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran()));
        if (isRestricted) {
            AlertHelper.showWarning("Akses Ditolak", "Peran Tidak Diizinkan", "Peran Anda tidak diizinkan untuk menambah resep obat.");
            return;
        }

        if (selectedPendaftaran != null && "selesai".equalsIgnoreCase(selectedPendaftaran.getStatus())) {
            AlertHelper.showWarning("Peringatan", "Pemeriksaan Selesai", "Pasien ini sudah selesai diperiksa hari ini.");
            return;
        }

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
        boolean isRestricted = SessionManager.isLoggedIn() && 
            ("perawat".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran()) || 
             "pasien".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran()));
        if (isRestricted) {
            AlertHelper.showWarning("Akses Ditolak", "Peran Tidak Diizinkan", "Peran Anda tidak diizinkan untuk menghapus resep obat.");
            return;
        }

        if (selectedPendaftaran != null && "selesai".equalsIgnoreCase(selectedPendaftaran.getStatus())) {
            AlertHelper.showWarning("Peringatan", "Pemeriksaan Selesai", "Pasien ini sudah selesai diperiksa hari ini.");
            return;
        }

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
        boolean isRestricted = SessionManager.isLoggedIn() && 
            ("perawat".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran()) || 
             "pasien".equalsIgnoreCase(SessionManager.getCurrentUser().getPeran()));
        if (isRestricted) {
            AlertHelper.showWarning("Akses Ditolak", "Peran Tidak Diizinkan", "Peran Anda tidak diizinkan untuk mengubah atau menyimpan rekam medis.");
            return;
        }

        if (selectedPendaftaran == null || selectedPasien == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Antrian", "Silakan pilih pasien dari tabel antrian terlebih dahulu!");
            return;
        }

        if ("selesai".equalsIgnoreCase(selectedPendaftaran.getStatus())) {
            AlertHelper.showWarning("Peringatan", "Pemeriksaan Selesai", "Pasien ini sudah selesai diperiksa hari ini.");
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
            
            // Keep the same patient selected, but reload details (which are now 'selesai')
            if (selectedPasien != null) {
                Pendaftaran updatedPendaftaran = null;
                for (Pendaftaran p : antrianTableView.getItems()) {
                    if (p.getIdPendaftaran() == rm.getIdPendaftaran()) {
                        updatedPendaftaran = p;
                        break;
                    }
                }
                
                if (updatedPendaftaran != null) {
                    antrianTableView.getSelectionModel().select(updatedPendaftaran);
                } else {
                    // Fallback: reload patient info directly (including history & read-only saved exam)
                    loadPatientInfo(selectedPasien.getIdPasien());
                }
            }
        } else {
            AlertHelper.showError("Error", "Gagal Menyimpan", "Terjadi kesalahan saat menyimpan rekam medis pemeriksaan.");
        }
    }
}
