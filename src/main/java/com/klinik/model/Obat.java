package com.klinik.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model untuk tabel: obat
 */
public class Obat {

    private int           idObat;
    private String        kodeObat;
    private String        namaObat;
    private String        kategori;
    private String        satuan;
    private int           stok;
    private BigDecimal    harga;
    private String        keterangan;
    private boolean       aktif;
    private LocalDateTime dibuatPada;
    private LocalDateTime diperbarui;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public Obat() {}

    public Obat(String kodeObat, String namaObat, String kategori,
                String satuan, int stok, BigDecimal harga) {
        this.kodeObat  = kodeObat;
        this.namaObat  = namaObat;
        this.kategori  = kategori;
        this.satuan    = satuan;
        this.stok      = stok;
        this.harga     = harga;
        this.aktif     = true;
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int        getIdObat()                   { return idObat; }
    public void       setIdObat(int id)             { this.idObat = id; }

    public String     getKodeObat()                 { return kodeObat; }
    public void       setKodeObat(String k)         { this.kodeObat = k; }

    public String     getNamaObat()                 { return namaObat; }
    public void       setNamaObat(String n)         { this.namaObat = n; }

    public String     getKategori()                 { return kategori; }
    public void       setKategori(String k)         { this.kategori = k; }

    public String     getSatuan()                   { return satuan; }
    public void       setSatuan(String s)           { this.satuan = s; }

    public int        getStok()                     { return stok; }
    public void       setStok(int s)                { this.stok = s; }

    public BigDecimal getHarga()                    { return harga; }
    public void       setHarga(BigDecimal h)        { this.harga = h; }

    public String     getKeterangan()               { return keterangan; }
    public void       setKeterangan(String k)       { this.keterangan = k; }

    public boolean    isAktif()                     { return aktif; }
    public void       setAktif(boolean a)           { this.aktif = a; }

    public LocalDateTime getDibuatPada()            { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)      { this.dibuatPada = d; }

    public LocalDateTime getDiperbarui()            { return diperbarui; }
    public void setDiperbarui(LocalDateTime d)      { this.diperbarui = d; }

    // Helper: stok menipis (di bawah 10)
    public boolean isStokMenupis() { return stok < 10; }

    @Override
    public String toString() {
        return "[" + kodeObat + "] " + namaObat + " — " + satuan;
    }
}
