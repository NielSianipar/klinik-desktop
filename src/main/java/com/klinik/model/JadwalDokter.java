package com.klinik.model;

import java.time.LocalTime;

/**
 * Model untuk tabel: jadwal_dokter
 */
public class JadwalDokter {

    private int       idJadwal;
    private int       idDokter;
    private String    hari;       // senin | selasa | ... | minggu
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private int       kuota;
    private boolean   tersedia;

    // Kolom join
    private String namaDokter;
    private String namaSpesialisasi;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public JadwalDokter() {}

    public JadwalDokter(int idDokter, String hari,
                        LocalTime jamMulai, LocalTime jamSelesai, int kuota) {
        this.idDokter   = idDokter;
        this.hari       = hari;
        this.jamMulai   = jamMulai;
        this.jamSelesai = jamSelesai;
        this.kuota      = kuota;
        this.tersedia   = true;
    }

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int       getIdJadwal()                  { return idJadwal; }
    public void      setIdJadwal(int id)            { this.idJadwal = id; }

    public int       getIdDokter()                  { return idDokter; }
    public void      setIdDokter(int id)            { this.idDokter = id; }

    public String    getHari()                      { return hari; }
    public void      setHari(String h)              { this.hari = h; }

    public LocalTime getJamMulai()                  { return jamMulai; }
    public void      setJamMulai(LocalTime t)       { this.jamMulai = t; }

    public LocalTime getJamSelesai()                { return jamSelesai; }
    public void      setJamSelesai(LocalTime t)     { this.jamSelesai = t; }

    public int       getKuota()                     { return kuota; }
    public void      setKuota(int k)               { this.kuota = k; }

    public boolean   isTersedia()                   { return tersedia; }
    public void      setTersedia(boolean t)         { this.tersedia = t; }

    public String    getNamaDokter()                { return namaDokter; }
    public void      setNamaDokter(String n)        { this.namaDokter = n; }

    public String    getNamaSpesialisasi()          { return namaSpesialisasi; }
    public void      setNamaSpesialisasi(String n)  { this.namaSpesialisasi = n; }

    // Helper: tampilkan jam dalam format HH:mm
    public String getJamTampil() {
        return jamMulai + " - " + jamSelesai;
    }

    @Override
    public String toString() {
        return hari + " " + getJamTampil() + " (kuota: " + kuota + ")";
    }
}
