package com.klinik.controller;

import com.klinik.dao.ObatDAO;
import com.klinik.model.Obat;
import com.klinik.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller untuk mengelola interaksi CRUD Apotek & Obat pada ObatView.
 * 
 * @author Daniel sianipar
 */
public class ObatController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Obat> obatTableView;

    @FXML
    private TableColumn<Obat, String> colKode;

    @FXML
    private TableColumn<Obat, String> colNama;

    @FXML
    private TableColumn<Obat, String> colKategori;

    @FXML
    private TableColumn<Obat, String> colSatuan;

    @FXML
    private TableColumn<Obat, Integer> colStok;

    // Form fields
    @FXML
    private TextField kodeField;

    @FXML
    private TextField namaField;

    @FXML
    private ComboBox<String> kategoriComboBox;

    @FXML
    private ComboBox<String> satuanComboBox;

    @FXML
    private TextField stokField;

    @FXML
    private TextField hargaField;

    @FXML
    private TextArea keteranganArea;

    @FXML
    private CheckBox aktifCheckBox;

    private final ObatDAO obatDAO = new ObatDAO();
    private Obat selectedObat = null;

    @FXML
    public void initialize() {
        // 1. Konfigurasi kolom tabel
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodeObat"));
        colNama.setCellValueFactory(new PropertyValueFactory<>("namaObat"));
        colKategori.setCellValueFactory(new PropertyValueFactory<>("kategori"));
        colSatuan.setCellValueFactory(new PropertyValueFactory<>("satuan"));
        colStok.setCellValueFactory(new PropertyValueFactory<>("stok"));

        // 2. Set item ComboBox
        kategoriComboBox.setItems(FXCollections.observableArrayList(
                "Obat Bebas", "Obat Bebas Terbatas", "Obat Keras", "Obat Wajib Apotek", "Narkotika"
        ));
        satuanComboBox.setItems(FXCollections.observableArrayList(
                "Tablet", "Kapsul", "Botol", "Sirup", "Tube", "Strip", "Pcs"
        ));

        // 3. Listener pilihan baris tabel
        obatTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedObat = newVal;
                showObatDetails(newVal);
            }
        });

        // 4. Load data awal
        loadObatData();
        clearForm();
    }

    private void loadObatData() {
        List<Obat> list = obatDAO.findAll();
        obatTableView.setItems(FXCollections.observableArrayList(list));
    }

    private void showObatDetails(Obat o) {
        kodeField.setText(o.getKodeObat());
        namaField.setText(o.getNamaObat());
        kategoriComboBox.setValue(o.getKategori());
        satuanComboBox.setValue(o.getSatuan());
        stokField.setText(String.valueOf(o.getStok()));
        hargaField.setText(o.getHarga() != null ? o.getHarga().toString() : "0");
        keteranganArea.setText(o.getKeterangan());
        aktifCheckBox.setSelected(o.isAktif());
    }

    private void clearForm() {
        selectedObat = null;
        kodeField.setText("");
        namaField.setText("");
        kategoriComboBox.setValue(null);
        satuanComboBox.setValue(null);
        stokField.setText("");
        hargaField.setText("");
        keteranganArea.setText("");
        aktifCheckBox.setSelected(true);
        obatTableView.getSelectionModel().clearSelection();
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadObatData();
        } else {
            List<Obat> list = obatDAO.search(query);
            obatTableView.setItems(FXCollections.observableArrayList(list));
        }
    }

    @FXML
    void handleNew(ActionEvent event) {
        clearForm();
    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selectedObat == null) {
            AlertHelper.showWarning("Peringatan", "Pilih Obat", "Silakan pilih obat yang ingin dihapus dari tabel terlebih dahulu.");
            return;
        }

        boolean confirm = AlertHelper.showConfirmation("Hapus Obat", "Konfirmasi Hapus", 
                "Apakah Anda yakin ingin menghapus data obat " + selectedObat.getNamaObat() + "?");
        
        if (confirm) {
            boolean success = obatDAO.delete(selectedObat.getIdObat());
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Dihapus", "Obat berhasil dihapus secara permanen.");
                loadObatData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menghapus", "Terjadi kesalahan saat menghapus data obat dari database.");
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String kode = kodeField.getText().trim();
        String nama = namaField.getText().trim();
        String stokStr = stokField.getText().trim();
        String hargaStr = hargaField.getText().trim();

        // Validasi input kosong
        if (kode.isEmpty() || nama.isEmpty() || stokStr.isEmpty() || hargaStr.isEmpty()) {
            AlertHelper.showWarning("Validasi Gagal", "Input Tidak Lengkap", "Kode, Nama, Stok, dan Harga Obat harus diisi!");
            return;
        }

        int stok;
        BigDecimal harga;

        // Validasi format angka untuk Stok & Harga
        try {
            stok = Integer.parseInt(stokStr);
            if (stok < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validasi Gagal", "Format Stok Salah", "Stok harus berupa angka bulat positif!");
            return;
        }

        try {
            harga = new BigDecimal(hargaStr);
            if (harga.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            AlertHelper.showWarning("Validasi Gagal", "Format Harga Salah", "Harga harus berupa angka desimal/bulat positif!");
            return;
        }

        String kategori = kategoriComboBox.getValue();
        String satuan = satuanComboBox.getValue();
        String keterangan = keteranganArea.getText().trim();
        boolean aktif = aktifCheckBox.isSelected();

        if (selectedObat == null) {
            // Tambah Baru (INSERT)
            Obat o = new Obat(kode, nama, kategori, satuan, stok, harga);
            o.setKeterangan(keterangan);
            o.setAktif(aktif);

            // Cek duplikasi kode
            if (obatDAO.findByKode(kode) != null) {
                AlertHelper.showWarning("Duplikasi Data", "Kode Obat Sudah Ada", "Obat dengan kode " + kode + " sudah terdaftar!");
                return;
            }

            boolean success = obatDAO.insert(o);
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Disimpan", "Obat baru berhasil ditambahkan.");
                loadObatData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Gagal menyimpan obat baru.");
            }
        } else {
            // Edit Data (UPDATE)
            // Jika kode diubah, cek duplikasi
            if (!selectedObat.getKodeObat().equalsIgnoreCase(kode)) {
                if (obatDAO.findByKode(kode) != null) {
                    AlertHelper.showWarning("Duplikasi Data", "Kode Obat Sudah Ada", "Obat dengan kode " + kode + " sudah terdaftar!");
                    return;
                }
            }

            selectedObat.setKodeObat(kode);
            selectedObat.setNamaObat(nama);
            selectedObat.setKategori(kategori);
            selectedObat.setSatuan(satuan);
            selectedObat.setStok(stok);
            selectedObat.setHarga(harga);
            selectedObat.setKeterangan(keterangan);
            selectedObat.setAktif(aktif);

            boolean success = obatDAO.update(selectedObat);
            if (success) {
                AlertHelper.showInfo("Sukses", "Data Diperbarui", "Data obat berhasil diperbarui.");
                loadObatData();
                clearForm();
            } else {
                AlertHelper.showError("Error", "Gagal Menyimpan", "Terjadi kesalahan saat memperbarui data obat.");
            }
        }
    }
}
