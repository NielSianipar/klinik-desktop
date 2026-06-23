package com.klinik.model;

import java.time.LocalDateTime;

/**
 * Model untuk tabel: dokter
 * Terhubung ke tabel pengguna dan spesialisasi
 */
public class Dokter {

    private int           idDokter;
    private int           idPengguna;
    private int           idSpesialisasi;
    private String        nomorSip;
    private String        telepon;
    private String        bio;
    private String        foto;
    private LocalDateTime dibuatPada;

    // Kolom join (tidak di tabel dokter, tapi diambil dari JOIN)
    private String namaLengkap;       // dari tabel pengguna
    private String namaSpesialisasi;  // dari tabel spesialisasi

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Dokter() {}

    public Dokter(int idPengguna, int idSpesialisasi, String nomorSip, String telepon) {
        this.idPengguna     = idPengguna;
        this.idSpesialisasi = idSpesialisasi;
        this.nomorSip       = nomorSip;
        this.telepon        = telepon;
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int    getIdDokter()                 { return idDokter; }
    public void   setIdDokter(int id)           { this.idDokter = id; }

    public int    getIdPengguna()               { return idPengguna; }
    public void   setIdPengguna(int id)         { this.idPengguna = id; }

    public int    getIdSpesialisasi()           { return idSpesialisasi; }
    public void   setIdSpesialisasi(int id)     { this.idSpesialisasi = id; }

    public String getNomorSip()                 { return nomorSip; }
    public void   setNomorSip(String n)         { this.nomorSip = n; }

    public String getTelepon()                  { return telepon; }
    public void   setTelepon(String t)          { this.telepon = t; }

    public String getBio()                      { return bio; }
    public void   setBio(String b)              { this.bio = b; }

    public String getFoto()                     { return foto; }
    public void   setFoto(String f)             { this.foto = f; }

    public LocalDateTime getDibuatPada()        { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)  { this.dibuatPada = d; }

    // Kolom join
    public String getNamaLengkap()              { return namaLengkap; }
    public void   setNamaLengkap(String n)      { this.namaLengkap = n; }

    public String getNamaSpesialisasi()         { return namaSpesialisasi; }
    public void   setNamaSpesialisasi(String n) { this.namaSpesialisasi = n; }

    @Override
    public String toString() {
        return "dr. " + namaLengkap + " — " + namaSpesialisasi;
    }
}
