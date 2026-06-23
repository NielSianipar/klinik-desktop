package com.klinik.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk tabel: pendaftaran
 */
public class Pendaftaran {

    private int           idPendaftaran;
    private int           idPasien;
    private int           idDokter;
    private int           idJadwal;
    private LocalDate     tanggalKunjungan;
    private String        nomorAntrian;
    private String        status;   // menunggu | dipanggil | selesai | batal
    private String        keluhan;
    private String        catatanAdmin;
    private LocalDateTime dibuatPada;
    private LocalDateTime diperbarui;

    // Kolom join
    private String namaPasien;
    private String nomorRm;
    private String namaDokter;
    private String namaSpesialisasi;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Pendaftaran() {}

    public Pendaftaran(int idPasien, int idDokter, int idJadwal,
                       LocalDate tanggalKunjungan, String nomorAntrian, String keluhan) {
        this.idPasien         = idPasien;
        this.idDokter         = idDokter;
        this.idJadwal         = idJadwal;
        this.tanggalKunjungan = tanggalKunjungan;
        this.nomorAntrian     = nomorAntrian;
        this.keluhan          = keluhan;
        this.status           = "menunggu";
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int        getIdPendaftaran()                { return idPendaftaran; }
    public void       setIdPendaftaran(int id)          { this.idPendaftaran = id; }

    public int        getIdPasien()                     { return idPasien; }
    public void       setIdPasien(int id)               { this.idPasien = id; }

    public int        getIdDokter()                     { return idDokter; }
    public void       setIdDokter(int id)               { this.idDokter = id; }

    public int        getIdJadwal()                     { return idJadwal; }
    public void       setIdJadwal(int id)               { this.idJadwal = id; }

    public LocalDate  getTanggalKunjungan()             { return tanggalKunjungan; }
    public void       setTanggalKunjungan(LocalDate t)  { this.tanggalKunjungan = t; }

    public String     getNomorAntrian()                 { return nomorAntrian; }
    public void       setNomorAntrian(String n)         { this.nomorAntrian = n; }

    public String     getStatus()                       { return status; }
    public void       setStatus(String s)               { this.status = s; }

    public String     getKeluhan()                      { return keluhan; }
    public void       setKeluhan(String k)              { this.keluhan = k; }

    public String     getCatatanAdmin()                 { return catatanAdmin; }
    public void       setCatatanAdmin(String c)         { this.catatanAdmin = c; }

    public LocalDateTime getDibuatPada()                { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)          { this.dibuatPada = d; }

    public LocalDateTime getDiperbarui()                { return diperbarui; }
    public void setDiperbarui(LocalDateTime d)          { this.diperbarui = d; }

    // Kolom join
    public String getNamaPasien()                       { return namaPasien; }
    public void   setNamaPasien(String n)               { this.namaPasien = n; }

    public String getNomorRm()                          { return nomorRm; }
    public void   setNomorRm(String n)                  { this.nomorRm = n; }

    public String getNamaDokter()                       { return namaDokter; }
    public void   setNamaDokter(String n)               { this.namaDokter = n; }

    public String getNamaSpesialisasi()                 { return namaSpesialisasi; }
    public void   setNamaSpesialisasi(String n)         { this.namaSpesialisasi = n; }

    @Override
    public String toString() {
        return "No." + nomorAntrian + " — " + namaPasien + " [" + status + "]";
    }
}
