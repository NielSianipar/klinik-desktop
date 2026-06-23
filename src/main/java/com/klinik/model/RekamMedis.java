package com.klinik.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model untuk tabel: rekam_medis
 */
public class RekamMedis {

    private int           idRekamMedis;
    private int           idPendaftaran;
    private int           idDokter;
    private int           idPasien;
    private LocalDate     tanggalPeriksa;
    private String        tekananDarah;
    private BigDecimal    suhuTubuh;
    private BigDecimal    beratBadan;
    private BigDecimal    tinggiBadan;
    private String        anamnesis;
    private String        pemeriksaanFisik;
    private String        diagnosis;
    private String        kodeIcd;
    private String        tindakan;
    private String        catatan;
    private LocalDateTime dibuatPada;
    private LocalDateTime diperbarui;

    // Kolom join
    private String namaPasien;
    private String nomorRm;
    private String namaDokter;

    // ----------------------------------------------------------------
    // Constructor
    // ----------------------------------------------------------------
    public RekamMedis() {}

    // ----------------------------------------------------------------
    // Getter & Setter
    // ----------------------------------------------------------------
    public int         getIdRekamMedis()                { return idRekamMedis; }
    public void        setIdRekamMedis(int id)          { this.idRekamMedis = id; }

    public int         getIdPendaftaran()               { return idPendaftaran; }
    public void        setIdPendaftaran(int id)         { this.idPendaftaran = id; }

    public int         getIdDokter()                    { return idDokter; }
    public void        setIdDokter(int id)              { this.idDokter = id; }

    public int         getIdPasien()                    { return idPasien; }
    public void        setIdPasien(int id)              { this.idPasien = id; }

    public LocalDate   getTanggalPeriksa()              { return tanggalPeriksa; }
    public void        setTanggalPeriksa(LocalDate t)   { this.tanggalPeriksa = t; }

    public String      getTekananDarah()                { return tekananDarah; }
    public void        setTekananDarah(String t)        { this.tekananDarah = t; }

    public BigDecimal  getSuhuTubuh()                   { return suhuTubuh; }
    public void        setSuhuTubuh(BigDecimal s)       { this.suhuTubuh = s; }

    public BigDecimal  getBeratBadan()                  { return beratBadan; }
    public void        setBeratBadan(BigDecimal b)      { this.beratBadan = b; }

    public BigDecimal  getTinggiBadan()                 { return tinggiBadan; }
    public void        setTinggiBadan(BigDecimal t)     { this.tinggiBadan = t; }

    public String      getAnamnesis()                   { return anamnesis; }
    public void        setAnamnesis(String a)           { this.anamnesis = a; }

    public String      getPemeriksaanFisik()            { return pemeriksaanFisik; }
    public void        setPemeriksaanFisik(String p)    { this.pemeriksaanFisik = p; }

    public String      getDiagnosis()                   { return diagnosis; }
    public void        setDiagnosis(String d)           { this.diagnosis = d; }

    public String      getKodeIcd()                     { return kodeIcd; }
    public void        setKodeIcd(String k)             { this.kodeIcd = k; }

    public String      getTindakan()                    { return tindakan; }
    public void        setTindakan(String t)            { this.tindakan = t; }

    public String      getCatatan()                     { return catatan; }
    public void        setCatatan(String c)             { this.catatan = c; }

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

    @Override
    public String toString() {
        return "RM-" + idRekamMedis + " | " + namaPasien + " | " + tanggalPeriksa;
    }
}
