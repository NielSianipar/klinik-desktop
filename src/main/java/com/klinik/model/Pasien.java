package com.klinik.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Model untuk tabel: pasien
 */
public class Pasien {

    private int           idPasien;
    private Integer       idPengguna;   // nullable
    private String        nomorRm;
    private String        namaLengkap;
    private LocalDate     tanggalLahir;
    private String        jenisKelamin; // laki-laki | perempuan
    private String        alamat;
    private String        telepon;
    private String        email;
    private String        golonganDarah;
    private String        riwayatAlergi;
    private LocalDateTime dibuatPada;
    private LocalDateTime diperbarui;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Pasien() {}

    public Pasien(String nomorRm, String namaLengkap, LocalDate tanggalLahir,
                  String jenisKelamin, String telepon) {
        this.nomorRm      = nomorRm;
        this.namaLengkap  = namaLengkap;
        this.tanggalLahir = tanggalLahir;
        this.jenisKelamin = jenisKelamin;
        this.telepon      = telepon;
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int      getIdPasien()                   { return idPasien; }
    public void     setIdPasien(int id)             { this.idPasien = id; }

    public Integer  getIdPengguna()                 { return idPengguna; }
    public void     setIdPengguna(Integer id)       { this.idPengguna = id; }

    public String   getNomorRm()                    { return nomorRm; }
    public void     setNomorRm(String n)            { this.nomorRm = n; }

    public String   getNamaLengkap()                { return namaLengkap; }
    public void     setNamaLengkap(String n)        { this.namaLengkap = n; }

    public LocalDate getTanggalLahir()              { return tanggalLahir; }
    public void      setTanggalLahir(LocalDate t)   { this.tanggalLahir = t; }

    public String   getJenisKelamin()               { return jenisKelamin; }
    public void     setJenisKelamin(String j)       { this.jenisKelamin = j; }

    public String   getAlamat()                     { return alamat; }
    public void     setAlamat(String a)             { this.alamat = a; }

    public String   getTelepon()                    { return telepon; }
    public void     setTelepon(String t)            { this.telepon = t; }

    public String   getEmail()                      { return email; }
    public void     setEmail(String e)              { this.email = e; }

    public String   getGolonganDarah()              { return golonganDarah; }
    public void     setGolonganDarah(String g)      { this.golonganDarah = g; }

    public String   getRiwayatAlergi()              { return riwayatAlergi; }
    public void     setRiwayatAlergi(String r)      { this.riwayatAlergi = r; }

    public LocalDateTime getDibuatPada()            { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)      { this.dibuatPada = d; }

    public LocalDateTime getDiperbarui()            { return diperbarui; }
    public void setDiperbarui(LocalDateTime d)      { this.diperbarui = d; }

    // ----------------------------------------------------------------
    // Helper: hitung usia otomatis dari tanggal lahir
    // ----------------------------------------------------------------
    public int getUsia() {
        if (tanggalLahir == null) return 0;
        return Period.between(tanggalLahir, LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return "[" + nomorRm + "] " + namaLengkap;
    }
}
