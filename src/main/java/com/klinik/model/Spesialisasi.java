package com.klinik.model;

/**
 * Model untuk tabel: spesialisasi
 */
public class Spesialisasi {

    private int    idSpesialisasi;
    private String namaSpesialisasi;
    private String keterangan;

    public Spesialisasi() {}

    public Spesialisasi(String namaSpesialisasi, String keterangan) {
        this.namaSpesialisasi = namaSpesialisasi;
        this.keterangan       = keterangan;
    }

    public int    getIdSpesialisasi()               { return idSpesialisasi; }
    public void   setIdSpesialisasi(int id)         { this.idSpesialisasi = id; }

    public String getNamaSpesialisasi()             { return namaSpesialisasi; }
    public void   setNamaSpesialisasi(String n)     { this.namaSpesialisasi = n; }

    public String getKeterangan()                   { return keterangan; }
    public void   setKeterangan(String k)           { this.keterangan = k; }

    @Override
    public String toString() { return namaSpesialisasi; }
}
