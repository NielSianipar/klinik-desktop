package com.klinik.controller;

import com.klinik.dao.DokterDAO;
import com.klinik.dao.JadwalDokterDAO;
import com.klinik.dao.PenggunaDAO;
import com.klinik.dao.SpesialisasiDAO;
import com.klinik.model.Dokter;
import com.klinik.model.JadwalDokter;
import com.klinik.model.Pengguna;
import com.klinik.model.Spesialisasi;
import com.klinik.util.AlertHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller untuk mengelola data dokter dan jadwal praktiknya.
 * 
 * @author Daniel sianipar
 */
public class DokterController {

    // TAB 1: Profil Dokter
    @FXML
    private TableView<Dokter> dokterTableView;

    @FXML
    private TableColumn<Dokter, String> colDokterNama;

    @FXML
    private TableColumn<Dokter, String> colDokterSpesialisasi;

    @FXML
    private TableColumn<Dokter, String> colDokterSip;

    @FXML
    private ComboBox<Pengguna> dokterUserComboBox;

    @FXML
    private ComboBox<Spesialisasi> dokterSpesialisasiComboBox;

    @FXML
    private TextField dokterSipField;

    @FXML
    private TextField dokterTeleponField;

    @FXML
    private TextArea dokterBioArea;

    // TAB 2: Jadwal Praktik
    @FXML
    private TableView<JadwalDokter> jadwalTableView;

    @FXML
    private TableColumn<JadwalDokter, String> colJadwalDokter;

    @FXML
    private TableColumn<JadwalDokter, String> colJadwalHari;

    @FXML
    private TableColumn<JadwalDokter, String> colJadwalJam;

    @FXML
    private TableColumn<JadwalDokter, Integer> colJadwalKuota;

    @FXML
    private ComboBox<Dokter> jadwalDokterComboBox;

    @FXML
    private ComboBox<String> jadwalHariComboBox;

    @FXML
    private TextField jadwalJamMulaiField;

    @FXML
    private TextField jadwalJamSelesaiField;

    @FXML
    private TextField jadwalKuotaField;

    @FXML
    private CheckBox jadwalTersediaCheckBox;

    // DAOs
    private final DokterDAO dokterDAO = new DokterDAO();
    private final JadwalDokterDAO jadwalDAO = new JadwalDokterDAO();
    private final PenggunaDAO penggunaDAO = new PenggunaDAO();
    private final SpesialisasiDAO spesialisasiDAO = new SpesialisasiDAO();

    // Selection states
    private Dokter selectedDokter = null;
    private JadwalDokter selectedJadwal = null;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // --- 1. SETUP TAB 1: DOKTER ---
        colDokterNama.setCellValueFactory(new PropertyValueFactory<>("namaLengkap"));
        colDokterSpesialisasi.setCellValueFactory(new PropertyValueFactory<>("namaSpesialisasi"));
        colDokterSip.setCellValueFactory(new PropertyValueFactory<>("nomorSip"));

        // Setup converters & items for ComboBoxes
        setupDokterComboBoxes();

        // Selection Listener
        dokterTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDokter = newVal;
                showDokterDetails(newVal);
            }
        });

        // --- 2. SETUP TAB 2: JADWAL ---
        colJadwalDokter.setCellValueFactory(new PropertyValueFactory<>("namaDokter"));
        colJadwalHari.setCellValueFactory(new PropertyValueFactory<>("hari"));
        colJadwalKuota.setCellValueFactory(new PropertyValueFactory<>("kuota"));
        
        // Custom rendering for Jam column
        colJadwalJam.setCellValueFactory(cellData -> {
            JadwalDokter jd = cellData.getValue();
            if (jd.getJamMulai() != null && jd.getJamSelesai() != null) {
                return new SimpleStringProperty(jd.getJamMulai().format(timeFormatter) + " - " + jd.getJamSelesai().format(timeFormatter));
            }
            return new SimpleStringProperty("-");
        });

        setupJadwalComboBoxes();

        // Selection Listener
        jadwalTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedJadwal = newVal;
                showJadwalDetails(newVal);
            }
        });

        // --- LOAD DATA ---
        loadDokterData();
        loadJadwalData();
        clearDokterForm();
        clearJadwalForm();
    }

    private void setupDokterComboBoxes() {
        // Tampilkan akun dengan peran = 'dokter'
        List<Pengguna> allUsers = penggunaDAO.findAll();
        List<Pengguna> dokterUsers = allUsers.stream()
                .filter(u -> "dokter".equalsIgnoreCase(u.getPeran()))
                .collect(Collectors.toList());
        dokterUserComboBox.setItems(FXCollections.observableArrayList(dokterUsers));

        // String converter agar menampilkan namaLengkap di ComboBox
        dokterUserComboBox.setConverter(new StringConverter<Pengguna>() {
            @Override public String toString(Pengguna object) {
                return object != null ? object.getNamaLengkap() + " (" + object.getEmail() + ")" : "";
            }
            @Override public Pengguna fromString(String string) { return null; }
        });

        // Tampilkan spesialisasi
        List<Spesialisasi> spesialisasiList = spesialisasiDAO.findAll();
        dokterSpesialisasiComboBox.setItems(FXCollections.observableArrayList(spesialisasiList));
        
        dokterSpesialisasiComboBox.setConverter(new StringConverter<Spesialisasi>() {
            @Override public String toString(Spesialisasi object) {
                return object != null ? object.getNamaSpesialisasi() : "";
            }
            @Override public Spesialisasi fromString(String string) { return null; }
        });
    }

    private void setupJadwalComboBoxes() {
        // Dropdown dokter
        List<Dokter> dokterList = dokterDAO.findAll();
        jadwalDokterComboBox.setItems(FXCollections.observableArrayList(dokterList));
        
        jadwalDokterComboBox.setConverter(new StringConverter<Dokter>() {
            @Override public String toString(Dokter object) {
                return object != null ? object.getNamaLengkap() + " (" + object.getNamaSpesialisasi() + ")" : "";
            }
            @Override public Dokter fromString(String string) { return null; }
        });

        // Dropdown hari
        jadwalHariComboBox.setItems(FXCollections.observableArrayList(
                "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu"
        ));
    }

    // --- PROFIL DOKTER CRUD ACTIONS ---

    private void loadDokterData() {
        List<Dokter> list = dokterDAO.findAll();
        dokterTableView.setItems(FXCollections.observableArrayList(list));
        
        // Segarkan dropdown dokter di Tab Jadwal juga
        jadwalDokterComboBox.setItems(FXCollections.observableArrayList(list));
    }

    private void showDokterDetails(Dokter d) {
        // Cari Pengguna yang cocok di ComboBox
        for (Pengguna u : dokterUserComboBox.getItems()) {
            if (u.getIdPengguna() == d.getIdPengguna()) {
                dokterUserComboBox.setValue(u);
                break;
            }
        }
        
        // Cari Spesialisasi yang cocok
        for (Spesialisasi s : dokterSpesialisasiComboBox.getItems()) {
            if (s.getIdSpesialisasi() == d.getIdSpesialisasi()) {
                dokterSpesialisasiComboBox.setValue(s);
                break;
            }
        }

        dokterSipField.setText(d.getNomorSip());
        dokterTeleponField.setText(d.getTelepon());
        dokterBioArea.setText(d.getBio());
    }

    private void clearDokterForm() {
        selectedDokter = null;
        dokterUserComboBox.setValue(null);
        dokterSpesialisasiComboBox.setValue(null);
        dokterSipField.setText("");
        dokterTeleponField.setText("");
        dokterBioArea.setText("");
        dokterTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleNewDokter(ActionEvent event) {
        clearDokterForm();
    }

    @FXML
    void handleDeleteDokter(ActionEvent event) {
        if (selectedDokter == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Dokter", "Silakan pilih dokter yang ingin dihapus terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Hapus Dokter", "Konfirmasi Hapus", 
                "Apakah Anda yakin ingin menghapus data profil dokter " + selectedDokter.getNamaLengkap() + "?");
        if (confirm) {
            boolean success = dokterDAO.delete(selectedDokter.getIdDokter());
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Dihapus", "Profil dokter berhasil dihapus.");
                loadDokterData();
                clearDokterForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menghapus", "Tidak dapat menghapus data dokter. Kemungkinan ada jadwal/antrian yang terikat.");
            }
        }
    }

    @FXML
    void handleSaveDokter(ActionEvent event) {
        Pengguna user = dokterUserComboBox.getValue();
        Spesialisasi spesialisasi = dokterSpesialisasiComboBox.getValue();
        String sip = dokterSipField.getText().trim();
        String telepon = dokterTeleponField.getText().trim();
        String bio = dokterBioArea.getText().trim();

        if (user == null || spesialisasi == null || sip.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Input Tidak Lengkap", "Akun Dokter, Spesialisasi, dan No. SIP harus diisi!");
            return;
        }

        if (selectedDokter == null) {
            // INSERT
            // Cek apakah user ini sudah punya profil dokter sebelumnya
            if (dokterDAO.findByPenggunaId(user.getIdPengguna()) != null) {
                AlertHelper.showWarning("Validasi Gagal", "Profil Sudah Ada", "Akun pengguna ini sudah terhubung ke profil dokter lain!");
                return;
            }

            Dokter d = new Dokter();
            d.setIdPengguna(user.getIdPengguna());
            d.setIdSpesialisasi(spesialisasi.getIdSpesialisasi());
            d.setNomorSip(sip);
            d.setTelepon(telepon);
            d.setBio(bio);

            boolean success = dokterDAO.insert(d);
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Disimpan", "Profil dokter baru berhasil ditambahkan.");
                loadDokterData();
                clearDokterForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal menambahkan profil dokter baru.");
            }
        } else {
            // UPDATE
            selectedDokter.setIdSpesialisasi(spesialisasi.getIdSpesialisasi());
            selectedDokter.setNomorSip(sip);
            selectedDokter.setTelepon(telepon);
            selectedDokter.setBio(bio);

            boolean success = dokterDAO.update(selectedDokter);
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Diperbarui", "Profil dokter berhasil diperbarui.");
                loadDokterData();
                clearDokterForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal memperbarui profil dokter.");
            }
        }
    }

    // --- JADWAL DOKTER CRUD ACTIONS ---

    private void loadJadwalData() {
        List<JadwalDokter> list = jadwalDAO.findAll();
        jadwalTableView.setItems(FXCollections.observableArrayList(list));
    }

    private void showJadwalDetails(JadwalDokter jd) {
        for (Dokter d : jadwalDokterComboBox.getItems()) {
            if (d.getIdDokter() == jd.getIdDokter()) {
                jadwalDokterComboBox.setValue(d);
                break;
            }
        }
        
        // Set Hari
        String hariFormatted = jd.getHari().substring(0, 1).toUpperCase() + jd.getHari().substring(1).toLowerCase();
        jadwalHariComboBox.setValue(hariFormatted);

        // Set Jam
        jadwalJamMulaiField.setText(jd.getJamMulai().format(timeFormatter));
        jadwalJamSelesaiField.setText(jd.getJamSelesai().format(timeFormatter));
        
        // Set Kuota
        jadwalKuotaField.setText(String.valueOf(jd.getKuota()));
        
        // Set Tersedia
        jadwalTersediaCheckBox.setSelected(jd.isTersedia());
    }

    private void clearJadwalForm() {
        selectedJadwal = null;
        jadwalDokterComboBox.setValue(null);
        jadwalHariComboBox.setValue(null);
        jadwalJamMulaiField.setText("");
        jadwalJamSelesaiField.setText("");
        jadwalKuotaField.setText("");
        jadwalTersediaCheckBox.setSelected(true);
        jadwalTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleNewJadwal(ActionEvent event) {
        clearJadwalForm();
    }

    @FXML
    void handleDeleteJadwal(ActionEvent event) {
        if (selectedJadwal == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Jadwal", "Silakan pilih jadwal praktik yang ingin dihapus terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Hapus Jadwal", "Konfirmasi Hapus", 
                "Apakah Anda yakin ingin menghapus jadwal untuk dokter " + selectedJadwal.getNamaDokter() + " pada hari " + selectedJadwal.getHari() + "?");
        if (confirm) {
            boolean success = jadwalDAO.delete(selectedJadwal.getIdJadwal());
            if (success) {
                AlertHelper.showInfo("Sukses", "Jadwal Dihapus", "Jadwal dokter berhasil dihapus.");
                loadJadwalData();
                clearJadwalForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menghapus", "Terjadi kesalahan saat menghapus jadwal.");
            }
        }
    }

    @FXML
    void handleSaveJadwal(ActionEvent event) {
        Dokter d = jadwalDokterComboBox.getValue();
        String hari = jadwalHariComboBox.getValue();
        String mulaiStr = jadwalJamMulaiField.getText().trim();
        String selesaiStr = jadwalJamSelesaiField.getText().trim();
        String kuotaStr = jadwalKuotaField.getText().trim();

        if (d == null || hari == null || mulaiStr.isEmpty() || selesaiStr.isEmpty() || kuotaStr.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Input Tidak Lengkap", "Dokter, Hari, Jam Mulai/Selesai, dan Kuota harus diisi!");
            return;
        }

        LocalTime jamMulai;
        LocalTime jamSelesai;
        int kuota;

        try {
            jamMulai = LocalTime.parse(mulaiStr, timeFormatter);
            jamSelesai = LocalTime.parse(selesaiStr, timeFormatter);
            
            if (jamSelesai.isBefore(jamMulai)) {
                AlertHelper.showWarning("Validasi Gagal", "Format Jam Salah", "Jam Selesai tidak boleh mendahului Jam Mulai!");
                return;
            }
        } catch (DateTimeParseException e) {
            AlertHelper.showWarning("Validasi Gagal", "Format Jam Salah", "Gunakan format jam 24-jam HH:mm (contoh: 08:30)!");
            return;
        }

        try {
            kuota = Integer.parseInt(kuotaStr);
            if (kuota <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validasi Gagal", "Format Kuota Salah", "Kuota antrian harus berupa angka bulat positif!");
            return;
        }

        boolean tersedia = jadwalTersediaCheckBox.isSelected();

        if (selectedJadwal == null) {
            // INSERT
            JadwalDokter jd = new JadwalDokter();
            jd.setIdDokter(d.getIdDokter());
            jd.setHari(hari.toLowerCase());
            jd.setJamMulai(jamMulai);
            jd.setJamSelesai(jamSelesai);
            jd.setKuota(kuota);
            jd.setTersedia(tersedia);

            boolean success = jadwalDAO.insert(jd);
            if (success) {
                AlertHelper.showInfo("Sukses", "Jadwal Ditambahkan", "Jadwal baru berhasil disimpan.");
                loadJadwalData();
                clearJadwalForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal menyimpan jadwal baru.");
            }
        } else {
            // UPDATE
            selectedJadwal.setIdDokter(d.getIdDokter());
            selectedJadwal.setHari(hari.toLowerCase());
            selectedJadwal.setJamMulai(jamMulai);
            selectedJadwal.setJamSelesai(jamSelesai);
            selectedJadwal.setKuota(kuota);
            selectedJadwal.setTersedia(tersedia);

            boolean success = jadwalDAO.update(selectedJadwal);
            if (success) {
                AlertHelper.showInfo("Sukses", "Jadwal Diperbarui", "Jadwal berhasil diperbarui.");
                loadJadwalData();
                clearJadwalForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal memperbarui jadwal.");
            }
        }
    }
}
