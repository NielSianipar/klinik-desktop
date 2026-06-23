package com.klinik.model;

import java.time.LocalDateTime;

/**
 * Model untuk tabel: riwayat_pengobatan
 * Detail obat yang diberikan per rekam medis
 */
public class RiwayatPengobatan {

    private int           idRiwayat;
    private int           idRekamMedis;
    private int           idObat;
    private int           jumlah;
    private String        aturanPakai;
    private String        catatanApoteker;
    private LocalDateTime dibuatPada;

    // Kolom join
    private String namaObat;
    private String satuan;
    private String kodeObat;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public RiwayatPengobatan() {}

    public RiwayatPengobatan(int idRekamMedis, int idObat,
                              int jumlah, String aturanPakai) {
        this.idRekamMedis = idRekamMedis;
        this.idObat       = idObat;
        this.jumlah       = jumlah;
        this.aturanPakai  = aturanPakai;
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int    getIdRiwayat()                    { return idRiwayat; }
    public void   setIdRiwayat(int id)              { this.idRiwayat = id; }

    public int    getIdRekamMedis()                 { return idRekamMedis; }
    public void   setIdRekamMedis(int id)           { this.idRekamMedis = id; }

    public int    getIdObat()                       { return idObat; }
    public void   setIdObat(int id)                 { this.idObat = id; }

    public int    getJumlah()                       { return jumlah; }
    public void   setJumlah(int j)                  { this.jumlah = j; }

    public String getAturanPakai()                  { return aturanPakai; }
    public void   setAturanPakai(String a)          { this.aturanPakai = a; }

    public String getCatatanApoteker()              { return catatanApoteker; }
    public void   setCatatanApoteker(String c)      { this.catatanApoteker = c; }

    public LocalDateTime getDibuatPada()            { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)      { this.dibuatPada = d; }

    // Kolom join
    public String getNamaObat()                     { return namaObat; }
    public void   setNamaObat(String n)             { this.namaObat = n; }

    public String getSatuan()                       { return satuan; }
    public void   setSatuan(String s)               { this.satuan = s; }

    public String getKodeObat()                     { return kodeObat; }
    public void   setKodeObat(String k)             { this.kodeObat = k; }

    @Override
    public String toString() {
        return namaObat + " — " + jumlah + " " + satuan + " (" + aturanPakai + ")";
    }
}
