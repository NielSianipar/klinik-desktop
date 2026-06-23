package com.klinik.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk tabel: laporan_kunjungan
 */
public class LaporanKunjungan {

    private int           idLaporan;
    private int           idDokter;
    private LocalDate     periodeMulai;
    private LocalDate     periodeSelesai;
    private int           totalKunjungan;
    private int           totalPasienBaru;
    private String        keterangan;
    private Integer       dibuatOleh;
    private LocalDateTime dibuatPada;

    // Kolom join
    private String namaDokter;
    private String namaSpesialisasi;
    private String namaPembuat;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public LaporanKunjungan() {}

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int        getIdLaporan()                    { return idLaporan; }
    public void       setIdLaporan(int id)              { this.idLaporan = id; }

    public int        getIdDokter()                     { return idDokter; }
    public void       setIdDokter(int id)               { this.idDokter = id; }

    public LocalDate  getPeriodeMulai()                 { return periodeMulai; }
    public void       setPeriodeMulai(LocalDate d)      { this.periodeMulai = d; }

    public LocalDate  getPeriodeSelesai()               { return periodeSelesai; }
    public void       setPeriodeSelesai(LocalDate d)    { this.periodeSelesai = d; }

    public int        getTotalKunjungan()               { return totalKunjungan; }
    public void       setTotalKunjungan(int t)          { this.totalKunjungan = t; }

    public int        getTotalPasienBaru()              { return totalPasienBaru; }
    public void       setTotalPasienBaru(int t)         { this.totalPasienBaru = t; }

    public String     getKeterangan()                   { return keterangan; }
    public void       setKeterangan(String k)           { this.keterangan = k; }

    public Integer    getDibuatOleh()                   { return dibuatOleh; }
    public void       setDibuatOleh(Integer id)         { this.dibuatOleh = id; }

    public LocalDateTime getDibuatPada()                { return dibuatPada; }
    public void setDibuatPada(LocalDateTime d)          { this.dibuatPada = d; }

    // Kolom join
    public String getNamaDokter()                       { return namaDokter; }
    public void   setNamaDokter(String n)               { this.namaDokter = n; }

    public String getNamaSpesialisasi()                 { return namaSpesialisasi; }
    public void   setNamaSpesialisasi(String n)         { this.namaSpesialisasi = n; }

    public String getNamaPembuat()                      { return namaPembuat; }
    public void   setNamaPembuat(String n)              { this.namaPembuat = n; }

    @Override
    public String toString() {
        return "Laporan dr." + namaDokter + " | " + periodeMulai + " s/d " + periodeSelesai;
    }
}
